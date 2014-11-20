package bcvapp

import grails.transaction.Transactional
import groovy.lang.Closure;
import groovy.io.FileType
import groovy.util.AntBuilder

import java.awt.event.ItemEvent;
import java.util.regex.Pattern;

import grails.util.Mixin
import grails.util.Holders

import org.codehaus.groovy.grails.web.context.ServletContextHolder as SCH





@Transactional
@Mixin(ServiceCategory)
class BcvjobService {

	def mailService
	def holderService
	def servletContext = SCH.servletContext
	def String absPath = getAbsPath()
	def String configPath = servletContext.getRealPath("/pipeline/bcvrun.prj.xml")

	def outputMap = [:]


	def prepareDirectory(Bcvjob job, String sessionId, List fileList, List directionList){

		def configFile = new File (configPath)

		def defaultConfig = new XmlParser().parse(configFile)



		//	Create directory structure

		def inputLine  = defaultConfig.BCV_Input
		def input = inputLine.text().replaceAll(Pattern.compile('[\\.\\/\\\\]'), '')

		def outputLine  = defaultConfig.BCV_Output
		def output = outputLine.text().replaceAll(Pattern.compile('[\\.\\/\\\\]'), '')

		def folderPath = "${absPath}${sessionId}"
		def inputPath = folderPath + "/" + input
		def outputPath = folderPath + "/" + output

		addResultsPath(sessionId, outputPath)

		inputLine[0].value = inputPath
		outputLine[0].value = outputPath

		def inpath  = defaultConfig.InPath // why there are two almost identical lines in bcv config?
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



	def Closure getWaitingPipeline = {Bcvjob job ->

		def queueSize = Bcvjob.countByDateCreatedLessThanEquals(job.dateCreated) + Stapjob.countByDateCreatedLessThanEquals(job.dateCreated)

		if(queueSize > 2){
			println (" bcv waiting in queue; sessionId ${job.sessionId} time ${System.currentTimeMillis()}")
			while (queueSize > 2){  // 1 running task + our task
				sleep(5000)
				queueSize = Bcvjob.countByDateCreatedLessThanEquals(job.dateCreated) + Stapjob.countByDateCreatedLessThanEquals(job.dateCreated)

			}
			println (" bcv finished waiting in queue; sessionId ${job.sessionId} time ${System.currentTimeMillis()}")
		}

		def returnCode = runPipeline(job.sessionId)
		println (" bcv waiting pipeline finished; sessionId ${job.sessionId} time ${System.currentTimeMillis()}")
		zipResults(job.sessionId)
		println (" bcv waiting results zipped; sessionId ${job.sessionId} time ${System.currentTimeMillis()}")
		if (returnCode == 0){

			if (job.email) {
				sendResults(job.email, job.sessionId)
				println (" bcv waiting results sent; sessionId ${job.sessionId} time ${System.currentTimeMillis()}")
			}
		}
		else {
			if (job.email) {
				sendLogs(job.email, job.sessionId)
				println (" bcv bad news sent; sessionId ${job.sessionId} time ${System.currentTimeMillis()}")
			}
		}
		job.delete(flush:true)
	}


	def Closure getPipeline = {Bcvjob job ->

		def returnCode = runPipeline(job.sessionId)
		println (" bcv pipeline finished; sessionId ${job.sessionId} time ${System.currentTimeMillis()}")
		zipResults(job.sessionId)
		println (" bcv results zipped; sessionId ${job.sessionId} time ${System.currentTimeMillis()}")
		if (returnCode == 0){
			
			if (job.email) {
				sendResults(job.email, job.sessionId)
				println (" bcv results sent; sessionId ${job.sessionId} time ${System.currentTimeMillis()}")
			}
		}
		else {
			if (job.email) {
				sendLogs(job.email, job.sessionId)
				println (" bcv bad news sent; sessionId ${job.sessionId} time ${System.currentTimeMillis()}")
			}
		}
		
		holderService.setDone(job.sessionId)
		job.delete(flush:true)
		
	}

	private def addResultsPath(String sessionId, String outputPath){
		outputMap.putAt(sessionId, outputPath)
	}



	def runPipeline(String sessionId){

		println (" going to run bcv pipeline, sessionId ${sessionId}")
		
//		StringWriter err = new StringWriter()
//		
//		'someExecutable.exe'.execute().waitForProcessOutput( System.out, err )
//		
//		if( err.toString() ) {
//		  println "ERRORS"
//		  println err
//		}
		
		def command = "perl /store/home/popova/Programs/BCV_pipeline/pipeline.pl ${absPath}${sessionId} bcvrun.prj.xml >${absPath}pipelinelog.txt >2${absPath}pipelinerr.txt"// Create the String
	
		//	try {
			def proc = command.execute()                 // Call *execute* on the string
			proc.consumeProcessOutput( System.out, System.err ) //31.10
			proc.waitFor()                               // Wait for the command to finish

		//	new File(absPath + "${sessionId}logfile").write("return code: ${ proc.exitValue()}\n stderr: ${proc.err.text}\n stdout: ${proc.in.text}")
		//	return proc.exitValue()
	//	} catch (Exception e){
	//		e.printStackTrace()
	//		return "Unexpected exception thrown by pipeline"
	//	}
		return proc.exitValue()
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

		def results = getZipResults(sessionId)
		println "going to send files, sessionID ${sessionId} resultsPath ${results} time ${System.currentTimeMillis()}"

		mailService.sendMail {
			multipart true
			to email
			subject "BCV results"
			body "Thanks for using BCV!\n Here are your results. \n Have a nice day!"
			attachBytes 'results.zip','application/zip', new File(results).readBytes()

		}

	}
	
	def sendLogs(String email, String sessionId) {
		
				//def logs = "${absPath}${sessionId}logfile"
				def results = getZipResults(sessionId)
				println "going to send logs, sessionID ${sessionId} logPath ${logs} time ${System.currentTimeMillis()}"
		
				mailService.sendMail {
					multipart true
					to email
					subject "BCV failed"
					body "We are very sorry, but something has gone amiss. Here are some of your results, though."
					attachBytes 'results.zip','application/zip', new File(results).readBytes()
				}
				//just in case there is no results at all and results.zip does not exist. Todo: catch mailService or zip exception
				mailService.sendMail {
					multipart true
					to "weidewind@gmail.com"
					subject "BCV failed"
					body "Achtung! email: ${email}, sessionId: ${sessionId}"
				}
				mailService.sendMail {
					multipart true
					to "weidewind@gmail.com"
					subject "BCV failed"
					body "Achtung! email: ${email}, sessionId: ${sessionId}"
					attachBytes 'results.zip','application/zip', new File(results).readBytes()
					
				}
				

		
			}


	def zipResults(String sessionId){

		def output = getOutput(sessionId)
		def results = getZipResults(sessionId)
		println "going to zip files, sessionID ${sessionId} time ${System.currentTimeMillis()}"
		println (output)

		def p = ~/.*\.(svg|with_names|cluster\.fasta)/
		def filelist = []


		def outputDir = new File(output)
		outputDir.eachDir { chrom ->
			def chromDir = new File(chrom.absolutePath)
			chromDir.eachFileMatch(FileType.FILES, p){ tree ->
				def splittedPath  = tree.absolutePath.split('/')
				println ("going to add ${splittedPath[splittedPath.size()-2]}/${splittedPath[splittedPath.size()-1]} from ${output} to zip list; sessionId ${sessionId}")
				filelist.add("${splittedPath[splittedPath.size()-2]}/${splittedPath[splittedPath.size()-1]}")
			}

		}
		filelist.add("simple_results.html")
		println("results will be placed here ${results}")
		def zipFile = new File("${results}")
		new AntBuilder().zip( basedir: output,
		destFile: zipFile.absolutePath,
		includes: filelist.join( ' ' ) )
	}



	//	def sendResults(String email, String sessionId) {
	//
	//		def resultsFilePath = getResults(sessionId)
	//		mailService.sendMail {
	//			multipart true
	//			to email
	//			subject "BCV results"
	//			body "Have a nice day!"
	//			attachBytes 'bcv_results.html','text/xml', new File(resultsFilePath).readBytes()
	//
	//		}
	//
	//	}

	def getResults (String sessionId){
		return outputMap.getAt(sessionId) + "/simple_results.html"
	}

	def getOutput(String sessionId){
		return outputMap.getAt(sessionId)
	}

	def getZipResults(String sessionId){
		return outputMap.getAt(sessionId) + "/results.zip"
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
		return Holders.config.absPath
	}

	def talkWork(){
		ServiceCategory.talkWork()
	}

	def talkQueue(){
		ServiceCategory.talkQueue()
	}

}

