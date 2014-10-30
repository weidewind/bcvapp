package bcvapp

import grails.transaction.Transactional
import groovy.lang.Closure;
import groovy.io.FileType
import groovy.util.AntBuilder

import java.awt.event.ItemEvent;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipOutputStream

import grails.util.Mixin
import grails.util.Holders

import org.codehaus.groovy.grails.web.context.ServletContextHolder as SCH




@Transactional
@Mixin(ServiceCategory)
class StapjobService {
	
	def mailService
	
	def servletContext = SCH.servletContext
	def String absPath = getAbsPath()
	def String configPath = servletContext.getRealPath("/pipeline/bcvrun.prj.xml")
	def String resultsPath
	def String outputPath
	def String zipResultsPath
	def String defaultName = "input"
	
	def prepareDirectory(Stapjob job, String sessionId, List fileList, List directionList){


		def configFile = new File (configPath)
		def defaultConfig = new XmlParser().parse(configFile)

		
		//	Create directory structure

		def inputLine  = defaultConfig.BCV_Input
		def input = inputLine.text().replaceAll(Pattern.compile('[\\.\\/\\\\]'), '')
		
		def outputLine  = defaultConfig.BCV_Output
		def output = outputLine.text().replaceAll(Pattern.compile('[\\.\\/\\\\]'), '')
		
		def folderPath = "${absPath}${sessionId}"
		def inputPath = folderPath + "/" + input
		def	outputPath = folderPath + "/" + output
		
		initResultsPath(outputPath)

		
		inputLine[0].value = inputPath
		outputLine[0].value = outputPath

		def inpath  = defaultConfig.InPath // why there are two identical lines in bcv config?
		inpath[0].value = inputPath
			
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
		
		def email = defaultConfig.Email
		email[0].value = job.email
		
		def mode = defaultConfig.Mode
		mode[0].value = "STAP"
		
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
	

//	def getPipeline(Stapjob job){
//		
//		def Closure myRunnable = { sessionId, email ->
//		
//				sleep (7000)
//				runSTAP(sessionId)
//
//				if (email != null) {
//					sendResults(email, "5")
//				}
//				
//				job.delete(flush:true)
//		
//			}
//	}
	
	
	private def initResultsPath(String pathToFile){
		resultsPath = pathToFile + "/simple_results.html"
		outputPath = pathToFile
		zipResultsPath = pathToFile + "/results.zip"
	}
	
	def runSTAP(String sessionId){
		
		def command = "perl /store/home/popova/Programs/BCV_pipeline/pipeline.pl ${absPath}${sessionId} bcvrun.prj.xml >${absPath}pipelinelog.txt >2${absPath}pipelinerr.txt"// Create the String
		def proc = command.execute()                 // Call *execute* on the string
		proc.waitFor()                               // Wait for the command to finish

		new File(absPath + "${sessionId}logfile").write("return code: ${ proc.exitValue()}\n stderr: ${proc.err.text}\n stdout: ${proc.in.text}")
		
                       // Wait for the command to finish
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
		
		def resultsFilePath = "${outputPath}/results.zip"

		mailService.sendMail {
		multipart true
		to email
		subject "STAP results"
		body "Thanks for using BCV!\n Here are your results. \n Have a nice day!"
		attachBytes 'results.zip','application/zip', new File(resultsFilePath).readBytes()
		
		}
		
	}
	
	
	def zipResults(String sessionId){
		
		println (outputPath)
		
		def p = ~/.*\.(svg|with_names|fasta)/
		def filelist = []
		def HOME = outputPath
		
				def outputDir = new File(outputPath)
				outputDir.eachDir { chrom ->
					def chromDir = new File(chrom.absolutePath)
					chromDir.eachFileMatch(FileType.FILES, p){ tree ->
						def splittedPath  = tree.absolutePath.split('/') 
						filelist.add("${splittedPath[splittedPath.size()-2]}/${splittedPath[splittedPath.size()-1]}")
					}
		
				}
				filelist.add("simple_results.html")
				println("${outputPath}/results.zip")
		def zipFile = new File("${outputPath}/results.zip")
		new AntBuilder().zip( basedir: HOME,
							  destFile: zipFile.absolutePath,
							  includes: filelist.join( ' ' ) )
	}
	
	def getResults (String sessionId){
		return resultsPath
		
	}
	
	def getZipResults(String sessionId){
		return zipResultsPath
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
	
	
//	def Closure getWaitingPipeline = {Stapjob job ->
//		
//					def queueSize = Bcvjob.countByDateCreatedLessThanEquals(job.dateCreated) + Stapjob.countByDateCreatedLessThanEquals(job.dateCreated)
//					
//					if(queueSize > 2){
//						while (queueSize > 2){  // 1 running task + our task
//							sleep(5000)
//							queueSize = Bcvjob.countByDateCreatedLessThanEquals(job.dateCreated) + Stapjob.countByDateCreatedLessThanEquals(job.dateCreated)
//						}
//					}
//					
//					runSTAP(job.sessionId)
//		
//					zipResults(job.sessionId)
//					
//					if (job.email) {
//						sendResults(job.email, job.sessionId)
//					}
//		
//					job.delete(flush:true)
//				}
//			
//	
//	def Closure getPipeline = {Stapjob job ->		
//				
//				runSTAP(job.sessionId)
//	
//				zipResults(job.sessionId)
//				
//				if (job.email) {
//					sendResults(job.email, job.sessionId)
//				}
//	
//				job.delete(flush:true)
//			}
	
	def Closure getWaitingPipeline = {Bcvjob job ->
		
					def queueSize = Bcvjob.countByDateCreatedLessThanEquals(job.dateCreated) + Stapjob.countByDateCreatedLessThanEquals(job.dateCreated)
					
					if(queueSize > 2){
						while (queueSize > 2){  // 1 running task + our task
							sleep(5000)
							queueSize = Bcvjob.countByDateCreatedLessThanEquals(job.dateCreated) + Stapjob.countByDateCreatedLessThanEquals(job.dateCreated)
							println (" stap waiting in queue; sessionId ${job.sessionId }")
							}
					}
					
					sleep (1000)
					runSTAP(job.sessionId)
					println (" stap waiting pipeline finished; sessionId ${job.sessionId }")
					
					zipResults(job.sessionId)
					println (" stap waiting results zipped; sessionId ${job.sessionId }")
					
					if (job.email) {
						sendResults(job.email, job.sessionId)
						println (" stap waiting results sent; sessionId ${job.sessionId }")
					}
		
					job.delete(flush:true)
				}
			
	
	def Closure getPipeline = {Bcvjob job ->
	
				runSTAP(job.sessionId)
				println (" stap pipeline finished; sessionId ${job.sessionId }")
	
				zipResults(job.sessionId)
				println (" stap results zipped; sessionId ${job.sessionId }")
				
				if (job.email) {
					sendResults(job.email, job.sessionId)
					println (" stap results sent; sessionId ${job.sessionId }")
				}
	
				job.delete(flush:true)
			}
	

	
def getAbsPath(){
		return Holders.config.absPath
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

