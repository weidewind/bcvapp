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
import org.apache.commons.io.IOUtils

import org.codehaus.groovy.grails.web.context.ServletContextHolder as SCH




@Transactional
class StapjobService {

	def mailService
	def holderService
	def servletContext = SCH.servletContext
	def String absPath = getAbsPath()
	def String configPath = servletContext.getRealPath("/pipeline/bcvrun.prj.xml")

	def outputMap = [:]

	def String defaultName = "input"

	def prepareDirectory(Stapjob job, String sessionId, List fileList, List directionList, Integer queueSize){


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

		addResultsPath(sessionId, outputPath)


		inputLine[0].value = inputPath
		outputLine[0].value = outputPath

		def inpath  = defaultConfig.InPath // why there are two identical lines in bcv config?
		inpath[0].value = inputPath

		new File (inputPath).mkdirs()
		new File (outputPath).mkdir()

		new File(outputPath + "/simple_results.html").createNewFile()
		File res = new File(outputPath + "/simple_results.html")
		if (queueSize > 2 ){
			res << ("Your task was submitted at  ${new Date()}<p>")
			res << ("Waiting in queue..<p>")
			res << ("Please, bookmark this page to see the results later. Refresh the page to check if they are ready.")
		}
		else {
			res << ("Your task was submitted at  ${new Date()}<p>")
			res << ("Running..<p>")
			res << ("Please, bookmark this page to see the results later. Refresh the page to check if they are ready.")	
		}
		
		res << ("<script>")
		res << ("var interval = setInterval('location.reload()', '30000');")
		res << ("</script>")
		
		if (fileList.size() > 0 && fileList[0].getOriginalFilename() != ""){
			for (f in fileList){
				new File (outputPath + "/" + f.getOriginalFilename().replaceAll(Pattern.compile('\\..*'), '').replaceAll("\\s+", "_")).mkdir()
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


	private def addResultsPath(String sessionId, String outputPath){
		outputMap.putAt(sessionId, outputPath)
	}

	def runSTAP(String sessionId){

		def command = "perl /store/home/popova/Programs/BCV_pipeline/pipeline.pl ${absPath}${sessionId} bcvrun.prj.xml >${absPath}pipelinelog.txt >2${absPath}pipelinerr.txt"// Create the String
//		try {
			holderService.procs[sessionId] = command.execute()                 // Call *execute* on the string
			holderService.procs[sessionId].consumeProcessOutput( System.out, System.err ) //31.10
			holderService.procs[sessionId].waitFor()                               // Wait for the command to finish

//		} catch (InterruptedException e){
//			e.printStackTrace()
//			return "interrupted"
//		}
//		catch (Exception e){
//			e.printStackTrace()
//			return "Unexpected exception thrown by pipeline"
//		}
		//		new File(absPath + "${sessionId}logfile").write("return code: ${ proc.exitValue()}\n stderr: ${proc.err.text}\n stdout: ${proc.in.text}")
		def exitValue = holderService.procs[sessionId].exitValue()
		println "return code " +  exitValue
		holderService.deleteProc(sessionId)
		return exitValue

	}


	def uploadFiles(String uploadPath, List fileList){
		for (f in fileList){
			if(f instanceof org.springframework.web.multipart.commons.CommonsMultipartFile){

				def fileName = f.getOriginalFilename().replaceAll("\\s+", "_")
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

		def resultsFilePath = getZipResults(sessionId)
		println "going to send files, sessionID ${sessionId} resultsPath ${resultsFilePath} time ${System.currentTimeMillis()}"
		mailService.sendMail {
			multipart true
			to email
			subject "STAP results"
			body "Thanks for using BCV!\n Here are your results. \n Have a nice day!"
			attachBytes 'results.zip','application/zip', new File(resultsFilePath).readBytes()

		}

	}

	def sendLogs(String email, String sessionId) {

		def logs = "${absPath}${sessionId}logfile"
		println "going to send logs, sessionID ${sessionId} logPath ${logs} time ${System.currentTimeMillis()}"

		mailService.sendMail {
			multipart true
			to email
			subject "STAP failed"
			body "We are very sorry, but something has gone amiss. Here are some of your results, though."
			attachBytes 'results.zip','application/zip', new File(results).readBytes()
		}

		 //else user left, auto-termination
			mailService.sendMail {
				multipart true
				to "weidewind@gmail.com"
				subject "STAP failed"
				body "Achtung! email: ${email}, sessionId: ${sessionId}"
			}
			mailService.sendMail {
				multipart true
				to "weidewind@gmail.com"
				subject "STAP failed"
				body "Achtung! email: ${email}, sessionId: ${sessionId}, returnCode ${returnCode}"
				attachBytes 'results.zip','application/zip', new File(results).readBytes()

			}
			println (" stap bad news sent to webmaster; sessionId ${job.sessionId} time ${System.currentTimeMillis()}")
		
		

	}

	def sendLogs(String sessionId){
		//just in case there is no results at all and results.zip does not exist. Todo: catch mailService or zip exception
		
			mailService.sendMail {
				multipart true
				to "weidewind@gmail.com"
				subject "BCV failed"
				body "Achtung! sessionId: ${sessionId}"
			}
			println (" stap bad news sent to webmaster; sessionId ${sessionId} time ${System.currentTimeMillis()}")

	}

//	def zipResults(String sessionId){
//		def output = getOutput(sessionId)
//		println "going to zip files, sessionID ${sessionId} time ${System.currentTimeMillis()}"
//		println (output)
//
//		def p = ~/.*\.(svg|with_names|cluster\.fasta)/
//		def filelist = []
//
//		def outputDir = new File(output)
//		outputDir.eachDir { chrom ->
//			def chromDir = new File(chrom.absolutePath)
//			chromDir.eachFileMatch(FileType.FILES, p){ tree ->
//				def splittedPath  = tree.absolutePath.split('/')
//				println ("going to add ${splittedPath[splittedPath.size()-2]}/${splittedPath[splittedPath.size()-1]} from ${output} to zip list; sessionId ${sessionId}")
//
//				filelist.add("${splittedPath[splittedPath.size()-2]}/${splittedPath[splittedPath.size()-1]}")
//			}
//
//		}
//		filelist.add("simple_results.html")
//		println("${output}/results.zip")
//		def zipFile = new File("${output}/results.zip")
//		new AntBuilder().zip( basedir: output,
//		destFile: zipFile.absolutePath,
//		includes: filelist.join( ' ' ) )
//	}

	def zipResults(String sessionId){
		
				def output = getOutput(sessionId)
				def results = getZipResults(sessionId)
				println "going to zip files, sessionID ${sessionId} time ${System.currentTimeMillis()}"
				println (output)
		
				def p = ~/.*\.(svg|html|with_names|cluster\.fasta)/
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
	
	def getResults (String sessionId){
		return outputMap.getAt(sessionId) + "/simple_results.html"
	}

	def getOutput(String sessionId){
		return outputMap.getAt(sessionId)
	}

	def getZipResults(String sessionId){
		return outputMap.getAt(sessionId) + "/results.zip"
	}


	def checkInput(Stapjob job, List fileList){
		String errorMessage = ""

		if (!job.validate()) {

			errorMessage = "Some errors occured: "

			job.errors.each {
				errorMessage += "<p>" +  it + "</p>"
			}
			if (!isFloat(job.distance) || job.distance.toFloat() < 0 || job.distance.toFloat() > 0.1){
				errorMessage += "<p> Maximum distance must not be less than 0 or more than 0.1 </p>"
			}
			if (job.errors.hasFieldErrors("email")){
				errorMessage += "<p>Your e-mail does not seem valid </p>"
			}
			if (job.errors.hasFieldErrors("files")){
				errorMessage += "<p>Select at least one file </p>"
			}
		}
		if (fileList.size() > 10){
			errorMessage += "<p>Please, do not select more than 10 files. </p>"
		}
		
		if (fileList.size() > 0 && fileList[0].getOriginalFilename() != ""){
			println (fileList.size())
			for (f in fileList) {
				println (f.getOriginalFilename())
				println (f)
				def name = f.getOriginalFilename()
				int dot= name.lastIndexOf(".");
				if (name.substring(dot+1) != "fasta"){
					errorMessage += "<p>Unsupported extension: ${name}}</p>"
				}

				def fileContents = IOUtils.toString(f.getInputStream(), "UTF-8")
				if (!isFasta(fileContents)){
					errorMessage += "<p>Not fasta: ${name} </p>"
					errorMessage += "<p>${fileContents}</p>"
				}
			}
		}

		if (job.sequences != null && !isFasta(job.sequences)){
			errorMessage += "<p>Sequences must be fasta-formatted </p>"
		}


		return errorMessage

	}

	def isFloat(String value)
	{
		 try
		 {
			 Float.parseFloat(value);
			 return true;
		  } catch(NumberFormatException nfe)
		  {
			  return false;
		  }
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

	def Closure getWaitingPipeline = {Stapjob job ->

		def queueSize = Bcvjob.countByDateCreatedLessThanEquals(job.dateCreated) + Stapjob.countByDateCreatedLessThanEquals(job.dateCreated)

		if(queueSize > 2){
			println (" stap waiting in queue; sessionId ${job.sessionId} time ${System.currentTimeMillis()}")
			while (queueSize > 2){  // 1 running task + our task
				sleep(5000)
				queueSize = Bcvjob.countByDateCreatedLessThanEquals(job.dateCreated) + Stapjob.countByDateCreatedLessThanEquals(job.dateCreated)

			}
			println (" stap finished waiting in queue; sessionId ${job.sessionId} time ${System.currentTimeMillis()}")
			
			def res = new File(getResults(job.sessionId)).newWriter()
				res.write("Your task was submitted at  ${new Date()}<p>")
				res.write("Running..<p>")
				res.write("Please, bookmark this page to see the results later. Refresh the page to check if they are ready.")
				res.close()
		}

		def returnCode = runSTAP(job.sessionId)
		println (" stap waiting pipeline finished; sessionId ${job.sessionId} time ${System.currentTimeMillis()}")
 
		if (returnCode != 143){
			zipResults(job.sessionId)
			println (" stap waiting results zipped; sessionId ${job.sessionId} time ${System.currentTimeMillis()}")
		}
		
		if (returnCode == 0){

			if (job.email) {
				sendResults(job.email, job.sessionId)
				println (" stap waiting results sent; sessionId ${job.sessionId} time ${System.currentTimeMillis()}")
			}
		}
		else {
			if (job.email) {
				sendLogs(job.email, job.sessionId)
				println ("stap bad news sent; sessionId ${job.sessionId} time ${System.currentTimeMillis()}")
			}
			else if (returnCode != 143) {
				sendLogs(job.sessionId) // send to weidewind
			}
			else {
				println (" user left; sessionId ${job.sessionId} ")
			}
		}
		
		def sessionId =  job.sessionId
		job.delete(flush:true)
		holderService.setDone(sessionId)
	}


	def Closure getPipeline = {Stapjob job ->

		def returnCode = runSTAP(job.sessionId)
		println (" stap returnCode " + returnCode)
		println (" stap pipeline finished; sessionId ${job.sessionId} time ${System.currentTimeMillis()}")
		if (returnCode != 143){
			zipResults(job.sessionId)
			println (" stap results zipped; sessionId ${job.sessionId} time ${System.currentTimeMillis()}")
		}
		if (returnCode == 0){
			
			if (job.email) {
				sendResults(job.email, job.sessionId)
				println (" stap results sent; sessionId ${job.sessionId} time ${System.currentTimeMillis()}")
			}
		}
		else {
			if (job.email) {
				sendLogs(job.email, job.sessionId, returnCode)
				println (" stap bad news sent; sessionId ${job.sessionId} time ${System.currentTimeMillis()}")
			}
			else if (returnCode != 143) {
				sendLogs(job.sessionId) // send to weidewind
			}
			else {
				println (" user left; sessionId ${job.sessionId} ")
			}
		}
		
		def sessionId =  job.sessionId
		job.delete(flush:true)
		holderService.setDone(sessionId)
	}

	def getAbsPath(){
		return Holders.config.absPath
	}

	def isFasta(String text){
		def isFasta = true
		def textArray = text.split(/[\r\n]+/)
		def switcher = 0; //0 - header expected, 1 - sequence expected, 2 - whatever
		for (str in textArray){
			str = str.trim()
			if (!isFasta){
				break
			}
			switch (switcher){
				case 1:
					if (! (str ==~   /[\sACTGNactgn-]+/)){
						isFasta = false
						break
					}
					else {
						switcher = 2
						break
					}

				case 2:
					if (! (str ==~   /^>[^>]+/)){
						if (! (str ==~   /[\sACTGNactgn-]+/)){
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
					if (! (str ==~   /^>[^>]+/)){
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



}

