package bcvapp

import grails.transaction.Transactional
import groovy.lang.Closure;

import java.awt.event.ItemEvent;
import java.util.regex.Pattern;
import grails.util.Mixin
import grails.util.Holders

import org.codehaus.groovy.grails.web.context.ServletContextHolder as SCH





@Transactional
@Mixin(ServiceCategory)
class BcvjobService {

	def mailService
	def servletContext = SCH.servletContext
	def String absPath = getAbsPath()
	def String configPath = servletContext.getRealPath("/pipeline/bcvrun.prj.xml")
	def String resultsPath

	def prepareDirectory(Bcvjob job, String sessionId, List fileList, List directionList){

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
		
		initResultsPath(outputPath)
		
		inputLine[0].value = inputPath
		outputLine[0].value = outputPath

		def inpath  = defaultConfig.InPath // why there are two identical lines in bcv config?
		inpath[0].value = inputPath

		new File (inputPath).mkdirs()
		new File (outputPath).mkdir()

		for (f in fileList){
			new File (outputPath + "/" + f.getOriginalFilename().replaceAll(Pattern.compile('\\..*'), '')).mkdir()
		}

		uploadFiles(inputPath, fileList)



		// Write custom configuration file



		def vocabulary = defaultConfig.Vocabulary
		def vocabularyPath = vocabulary[0].text().substring(0, vocabulary[0].text().lastIndexOf("/")+1)
		def vocabularyName = job.vocabulary.replaceAll(/\s+/, "_")
		vocabulary[0].value = vocabularyPath + vocabularyName + ".seq.fas"

		def reads = defaultConfig.READS.find{'READS'}
		reads.children.clear()

		def counter = 0
		for (f in fileList){
			def stringDirection
			if (directionList.get(counter) == null){
				stringDirection = "reverse"
			}
			else stringDirection = "forward"
			reads.appendNode('Read', [name:f.getOriginalFilename()]).appendNode('Direction', stringDirection)
			counter++
		}


		def taxdb = defaultConfig.Database
		if (job.taxdb == "full"){
			taxdb[0].value = "all"
		}
		else if (job.taxdb == "named isolates"){
			taxdb[0].value = "named"
		}


		def distance = defaultConfig.DistanceThreshold
		distance[0].value = job.distance.toFloat()
		
		def email = defaultConfig.Email
		email[0].value = job.email
		
		def mode = defaultConfig.Mode
		mode[0].value = "PIPELINE"

		def writer = new StringWriter()
		def printer = new XmlNodePrinter(new PrintWriter(writer))
		printer.with {
				preserveWhitespace = true
				expandEmptyElements = true
		}
		printer.print(defaultConfig)
		def result = writer.toString()
		new File(folderPath + "/bcvrun.prj.xml").write(result)

		return outputPath

	}

//  it workes somehow
	
//	def getPipeline(Bcvjob job){
//
//		def Closure myRunnable = {sessionId, email ->
//
//			sleep (10000)
//			runPipeline(sessionId)
//
//			if (email != null) {
//				sendResults(email, sessionId)
//			}
//
//			job.delete(flush:true)
//		}
//		
//		return myRunnable
//	}

	
	def Closure getWaitingPipeline = {Bcvjob job ->
		
					def queueSize = Bcvjob.countByDateCreatedLessThanEquals(job.dateCreated) + Stapjob.countByDateCreatedLessThanEquals(job.dateCreated)
					
					if(queueSize > 2){
						while (queueSize > 2){  // 1 running task + our task
							sleep(5000)
							queueSize = Bcvjob.countByDateCreatedLessThanEquals(job.dateCreated) + Stapjob.countByDateCreatedLessThanEquals(job.dateCreated)
						}
					}
					
					sleep (1000)
					runPipeline(job.sessionId)
		
					if (job.email) {
						sendResults(job.email, job.sessionId)
					}
		
					job.delete(flush:true)
				}
			
	
	def Closure getPipeline = {Bcvjob job ->
	
				
				sleep (1000)
				runPipeline(job.sessionId)
	
				if (job.email) {
					sendResults(job.email, job.sessionId)
				}
	
				job.delete(flush:true)
			}
	


	private def initResultsPath(String pathToFile){
		resultsPath = pathToFile + "/simple_results.html"
	}
	
	def runPipeline(String sessionId){
		
		def command = "perl /store/home/popova/Programs/BCV_pipeline/pipeline.pl ${absPath}${sessionId} bcvrun.prj.xml >${absPath}pipelinelog.txt >2${absPath}pipelinerr.txt"// Create the String
		def proc = command.execute()                 // Call *execute* on the string
		proc.waitFor()                               // Wait for the command to finish

		new File(absPath + "${sessionId}logfile").write("return code: ${ proc.exitValue()}\n stderr: ${proc.err.text}\n stdout: ${proc.in.text}")
		
	}


	def uploadFiles(String uploadPath, List fileList){

		for (f in fileList){
			if(f instanceof org.springframework.web.multipart.commons.CommonsMultipartFile){

				def fileName = f.getOriginalFilename()
				new FileOutputStream(uploadPath + "/" + fileName).leftShift( f.getInputStream() )

			} else {
				log.error("wrong attachment type [${f.getClass()}]");
			}
		}
	}

	def uploadSequenceFiles(String uploadPath, List fileList){
		for (f in fileList){
			if(f instanceof org.springframework.web.multipart.commons.CommonsMultipartFile){

				def fileName = f.getOriginalFilename()
				new FileOutputStream(uploadPath + "/" + fileName.replaceAll(Pattern.compile('\\..*'), '') + "/" + fileName).leftShift( f.getInputStream() )

			} else {
				log.error("wrong attachment type [${f.getClass()}]");
			}
		}
	}





	def sendResults(String email, String sessionId) {
		
		def resultsFilePath = getResults(sessionId)
		mailService.sendMail {
			multipart true
			to email
			subject "BCV results"
			body "Have a nice day!"
			attachBytes 'bcv_results.html','text/xml', new File(resultsFilePath).readBytes()

		}

	}
	def getResults (String sessionId){
		//def resultsPath = "${absPath}${sessionId}" + "/simple_results.html"
		return resultsPath

	}

	def checkInput(Bcvjob job, List fileList){
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
		for (f in fileList) {
			def name = f.getOriginalFilename()
			int dot= name.lastIndexOf(".");
			if (name.substring(dot+1) != "ab1"){
				errorMessage += "<p>Unsupported extension: ${name} </p>"
			}

			def bytes = f.getBytes()
			if (bytes[0] != 'A' || bytes[1] != 'B' || bytes[2] != 'I' || bytes[3] != 'F' || !(bytes[4]+bytes[5] >= 101)){
				errorMessage += "<p>Not ABI: ${name} </p>"
			}

			
		}


		return errorMessage

	}
	
	def getAbsPath(){
		def absPath = ""
		//def pathArray = servletContext.getRealPath("/pipeline").split("\\\\")
		//for (int i = 0; i < pathArray.size() - 3; i++){
		//	absPath += pathArray[i] + "\\"
		//}
		
//		def pathArray = servletContext.getRealPath("").split("\\\\")
//		for (int i = 0; i < pathArray.size()-2; i++){
//			absPath += pathArray[i] + "\\"
//		}
	//	return servletContext.getRealPath("")
		return Holders.config.absPath
	}
	
	def talkWork(){
		ServiceCategory.talkWork()
	}
	
	def talkQueue(){
		ServiceCategory.talkQueue()
	}

}

