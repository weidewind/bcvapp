package bcvapp

import grails.transaction.Transactional
import groovy.lang.Closure;

import java.awt.event.ItemEvent;
import java.util.List;
import java.util.regex.Pattern;
import grails.util.Mixin

import org.codehaus.groovy.grails.web.context.ServletContextHolder as SCH




@Transactional
@Mixin(ServiceCategory)
class StapjobService {
	
	def mailService
	
	def servletContext = SCH.servletContext
	def String absPath = getAbsPath()
	def String configPath = servletContext.getRealPath("/pipeline/bcvrun.prj.xml")

	def prepareDirectory(Stapjob job, String sessionId, List fileList, List directionList){

		def outputPath = ""

		def configFile = new File (configPath)

		def defaultConfig = new XmlParser().parse(configFile)

		

		
		//	Create directory structure

		def inputLine  = defaultConfig.BCV_Input
		def input = inputLine.text().replaceAll(Pattern.compile('[\\.\\/\\\\]'), '')
		
		def outputLine  = defaultConfig.BCV_Output
		def output = outputLine.text().replaceAll(Pattern.compile('[\\.\\/\\\\]'), '')
		
		def folderPath = "${absPath}${sessionId}"
		def inputPath = folderPath + "/" + input
			outputPath = folderPath + "/" + output
		
		new File (inputPath).mkdirs()
		new File (outputPath).mkdir()
		
		if (fileList != null){
			for (f in fileList){
				new File (outputPath + "/" + f.getOriginalFilename().replaceAll(Pattern.compile('\\..*'), '')).mkdir()
			}
			uploadFiles(outputPath, fileList)
		}
		
		if (job.sequences != null) { 
			def defaultOutput = outputPath + "/" + defaultName
			new File (defaultOutput).mkdir()
			uploadSequences(defaultOutput, job.sequences)
		}
		

		
		
		// Write custom configuration file


		def taxdb = defaultConfig.Database
		if (job.taxdb == "full"){
			taxdb[0].value = "all"
		}
		else if (job.taxdb == "named isolates"){
			taxdb[0].value = "named"
		}
		
		
		def distance = defaultConfig.DistanceThreshold
		distance[0].value = job.distance.toFloat()
		
		def writer = new StringWriter()
		def printer = new XmlNodePrinter(new PrintWriter(writer))
		printer.preserveWhitespace = false
		printer.print(defaultConfig)
		def result = writer.toString()
		new File(folderPath + "/bcvrun.prj.xml").write(result)
		
		return outputPath
		
	}
	

	def getPipeline(Stapjob job){
		
		def Closure myRunnable = { sessionId, email ->
		
				sleep (7000)
				runSTAP(sessionId)

				if (email != null) {
					sendResults(email, "5")
				}
				
				job.delete(flush:true)
		
			}
	}
	
	
	def runSTAP(String sessionId){
		
		sleep (15000)
//		def command = "cmd /c C:/Users/weidewind/workspace/test/email.pl"// Create the String
//		def proc = command.execute()                 // Call *execute* on the string
//		proc.waitFor()                               // Wait for the command to finish
	}
	
	
	def uploadFiles(String uploadPath, List fileList){
		for (f in fileList){
			if(f instanceof org.springframework.web.multipart.commons.CommonsMultipartFile){
			
				def fileName = f.getOriginalFilename()
				new FileOutputStream(uploadPath + "/" + fileName.replaceAll(Pattern.compile('\\..*'), '') + "/" + fileName).leftShift( f.getInputStream() )

			} else {
				log.error("wrong attachment type [${f.getClass()}]");
			}
		}
	}
	
	
	def uploadSequences(String uploadPath, String sequences){
		final FileOutputStream fos = new FileOutputStream(uploadPath + "/" + defaultName + ".ab1.cluster.fasta")
		final PrintStream printStream = new PrintStream(fos)
		printStream.print(sequences)
		printStream.close()
	}

	
	def sendResults(String email, String sessionId) {
		
		def resultsFilePath = "${absPath}${sessionId}" + "/simple_results.html"
		mailService.sendMail {
		multipart true
		to email
		subject "BCV results"
		body "Have a nice day!"
		attachBytes resultsFilePath,'text/xml', new File(resultsFilePath).readBytes()
		
		}
		
	}
	
	def getResults (String sessionId){
		def resultsPath = "${absPath}${sessionId}" + "/simple_results.html"
		return resultsPath
		
	}
	

	
	def checkInput(Stapjob job, List fileList){
		String errorMessage = ""
		
		if (!job.validate()) {

		errorMessage = "Some errors occured: "

		job.errors.each {
			errorMessage += "<p>" +  it + "</p>"
		}
		if (!job.distance.isFloat() || job.distance.toFloat() < 0 || job.distance.toFloat() > 0.1){
			errorMessage += "<p> Maximum distance must not be less than 0 or more than 0.1 </p>"
		}
		if (job.errors.hasFieldErrors("email")){
			errorMessage += "<p>Your e-mail does not seem valid </p>"
		}
		if (job.errors.hasFieldErrors("files")){
			errorMessage += "<p>Select at least one file </p>"
		}
		}
		if (fileList.size() > 1){
			for (f in fileList) {
				def name = f.getOriginalFilename()
				int dot= name.lastIndexOf(".");
				if (name.substring(dot+1) != "fasta"){
					errorMessage += "<p>Unsupported extension: ${name}}</p>"
				}
				
				def fileContents = f.text
				if (!fileContents.isFasta()){
					errorMessage += "<p>Not fasta: ${name} </p>"
				}
			}
		}

		if (job.sequences != null && !isFasta(job.sequences)){
			errorMessage += "<p>Sequences must be fasta-formatted </p>"
		}
		

		return errorMessage
		
	}
	
	
	def Closure getWaitingPipeline = {Stapjob job ->
		
					def queueSize = Bcvjob.countByDateCreatedLessThanEquals(job.dateCreated) + Stapjob.countByDateCreatedLessThanEquals(job.dateCreated)
					
					if(queueSize > 2){
						while (queueSize > 2){  // 1 running task + our task
							sleep(5000)
							queueSize = Bcvjob.countByDateCreatedLessThanEquals(job.dateCreated) + Stapjob.countByDateCreatedLessThanEquals(job.dateCreated)
						}
					}
					
					sleep (10000)
					runSTAP(job.sessionId)
		
					if (job.email) {
						sendResults(job.email, job.sessionId)
					}
		
					job.delete(flush:true)
				}
			
	
	def Closure getPipeline = {Stapjob job ->
	
				
				sleep (10000)
				runSTAP(job.sessionId)
	
				if (job.email) {
					sendResults(job.email, job.sessionId)
				}
	
				job.delete(flush:true)
			}
	

	
	def getAbsPath(){
		def absPath = ""
		def pathArray = servletContext.getRealPath("/pipeline").split("\\\\")
		for (int i = 0; i < pathArray.size() - 3; i++){
			absPath += pathArray[i] + "\\"
		}
		return absPath
	}
	
	def isFasta(String text){
		def isFasta = true
		def textArray = text.split(/[\r\n]+/)
		def switcher = 0; //0 - header expected, 1 - sequence expected, 2 - whatever
		for (str in textArray){
			if (!isFasta){
				break
			}
			switch (switcher){
				case 1:
				if (! (str ==~   /[ACTGNactgn-]+/)){
					isFasta = false
					break
				}
				else {
					switcher = 2
					break
				}
	
				case 2:
				if (! (str ==~   /^>[\w-]+/)){
					if (! (str ==~   /[ACTGNactgn-]+/)){
						isFasta = false
						break
					}
					else {
						switcher = 2
						break
					}
				}
				else {
					switcher = 1
					break
				}
				case 0:
				if (! (str ==~   /^>[\w-]+/)){
					isFasta = false
					break
				}
				else {
					switcher = 1
					break
				}
				default:
					isFasta = false	
			}

		}
		return isFasta
	}
	
	def talkWork(){
		ServiceCategory.talkWork()
	}
	
	def talkQueue(){
		ServiceCategory.talkWork()
	}

}

