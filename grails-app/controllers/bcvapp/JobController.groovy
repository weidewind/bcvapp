package bcvapp

import groovyx.gpars.GParsPool
import jsr166y.ForkJoinPool

import java.util.concurrent.Future

import groovy.lang.Closure;
import groovy.transform.Synchronized;
import grails.util.Holders


class JobController {

	def bcvjobService
	def stapjobService
	def holderService
	
	final def timeStampMap = [:]



	def index = {
		redirect(action: form)
	}

	def submitbcv() {
		def job = new Bcvjob(params)
		def jobService = bcvjobService
		submit(job, jobService)
	}

	def submitstap() {
		def job = new Stapjob(params)
		def jobService = stapjobService
		submit(job, jobService)
	}

	def submit(Object job, Object jobService) {

		def sessionId = jobService.createSessionId()
		//def sessionId = "8abc"
		job.setSessionId(sessionId)

		// Get files and directions
		def fileListRaw = []
		if (params.('isExamaple') == "true"){
			def exampleFileNames = params.('exampleFiles')
			for (int i = 0; i < exampleFileNames.size(); i++){
				println (exampleFileNames[i].name)
				fileListRaw.add(new File(Holders.config.storePath + exampleFileNames[i].name))
			}
		}
		
		else {
			fileListRaw = request.getFiles('files');
		}
		
		def  directionListRaw = []
		for (int i = 0; i < fileListRaw.size(); i++){
			directionListRaw.add(params.('radio' + i))
			println ("button " + i + " " + params.('radio' + i))
		}

		// delete files (and directions for them) which were removed by user (file input is readonly!)
		def fileList = []
		def  directionList = []
		def removed = params.('deletedFiles')
		if (removed){
			removed = removed.substring(1).split(',')
		}
		for(i in removed){
			fileListRaw[i.toInteger()] = null
			directionListRaw[i.toInteger()] = null
		}
		for (int i = 0; i < fileListRaw.size(); i++){
			if (fileListRaw[i]){
				fileList.add(fileListRaw[i])
				directionList.add(directionListRaw[i])
				
			}
		}
		//
		
		def errorMessage = jobService.checkInput(job, fileList)

		if (errorMessage) {
			render "${errorMessage}"
			return
		}
		
		if (!params.checkbox_email){
			job.setEmail("")
		}
		
		
		job.save()
		
		if (params.('isExamaple') == "false"){
			int queueSize = Bcvjob.countByDateCreatedLessThanEquals(job.dateCreated) + Stapjob.countByDateCreatedLessThanEquals(job.dateCreated)
			jobService.prepareDirectory(job, sessionId, fileList, directionList, queueSize)

			if (job.email){
				runAsync(job, jobService)
			}
			else {
				run (job, jobService, queueSize)
			}

		}
		else {
			def folderName = jobService.getExampleFolderName(fileList)
			if (job.email){
				jobService.sendExampleResults(job.email, folderName)
			}
			else {
				renderExample(folderName)
			}
		}
	}


	
	def renderExample(Object jobService, String folderName){
		def resultsPath = jobService.getExampleResults(folderName)
		def zipResultsPath = jobService.getExampleZipResults(folderName)
		def path = resultsPath.split('/')
		def pathEnd = path[path.length-3] + "/" + path[path.length-2]
		def url = createLink(controller: 'job', action: 'renderExampleResults', params: [pathEnd:pathEnd, folderName:folderName])
		render(contentType: 'text/html', text: "<script>window.location.href='$url'</script>")
		
	}
	
	
	
	def renderResults (String pathEnd, String sessionId){

		def htmlContent = new File(Holders.config.absPath + pathEnd + "/simple_results.html").text
//		def matcher = (htmlContent =~ /<a href="(\.\/)*?">/);
//		htmlContent = matcher.replaceAll("");
//		matcher = (htmlContent =~ /Length: *?nt/);
//		htmlContent = matcher.replaceAll("");
//		render ("<a href='${createLink(action: 'downloadFile' , params: [path: zipResultsPath, contentType: 'application/zip', filename: 'results.zip'])}'>Download all files</a> (.fasta files, trees and the report itself) <p></p>")
		
		def matcher = (htmlContent =~ /<a href=\"\.\//);
		htmlContent = matcher.replaceAll('<a href="http://bcvapp.cmd.su/static/web-app/results/'+pathEnd+"/");
		
		render (text: htmlContent, contentType:"text/html", encoding:"UTF-8")
		
		
	
//		def htmlContent = new File(resultsPath).text
//		render ("<a href='${createLink(action: 'downloadFile' , params: [zipResultsPath: zipResultsPath])}'>Download all files</a> (.fasta files, trees and the report itself) <p></p>")
//		
//		render (text: htmlContent, contentType:"text/html", encoding:"UTF-8")
	}

	def renderExampleResults (String pathEnd, String folderName){
		
				def htmlContent = new File(Holders.config.storePath + pathEnd + "/simple_results.html").text
					
				def matcher = (htmlContent =~ /<a href=\"\.\//);
				htmlContent = matcher.replaceAll('<a href="http://bcvapp.cmd.su/static/web-app/examples/'+pathEnd+"/");
				
				render (text: htmlContent, contentType:"text/html", encoding:"UTF-8")
			}
	
//	def downloadZipFile(String zipResultsPath){
//		def file = new File(zipResultsPath)
//		response.setContentType("application/zip")
//		response.setHeader("Content-disposition", "filename='results.zip'")
//		response.outputStream << file.getBytes()
//		response.outputStream.flush()
//	}
	
	def downloadFile(String path, String contentType, String filename){
		def file = new File(path)
		def outputStream = null
		try {
		response.setContentType(contentType)
		response.setHeader("Content-disposition", "filename='${filename}'")
		outputStream = response.outputStream
		outputStream << file.getBytes()
	
		} catch (IOException e){
			println('Canceled download? '+ e)
		} finally {
			if (outputStream != null){
				try {
					outputStream.flush();
					outputStream.close()
				} catch (IOException e) {
					println('Exception on close '+ e)
				}
			}
		}

	}
	
	def downloadChrom(String filename){
		downloadFile(Holders.config.storePath + filename, "application/x-dna", filename)
	}
	
	def askforemail = {
	

	}
	


	
	def deleteJob(String id, String task){
		def job
		
		if (task == "class bcvapp.Bcvjob"){
			job = Bcvjob.get(id)
		}
		else if (task == "class bcvapp.Stapjob"){
			job = Stapjob.get(id)
		}
		
		job.delete(flush:true)
	}

	def updateAndRun(){
		def job
		def jobService
		
		if (params.task == "class bcvapp.Bcvjob"){
			job = Bcvjob.get(params.id)
			jobService = bcvjobService
		}
		else if (params.task == "class bcvapp.Stapjob"){
			job = Stapjob.get(params.id)
			jobService = stapjobService
		}
		if (params.email){
			job.email = params.email
			job.save(flush:true)
			runAsync (job, jobService)
		}
		else {
			//render "Your task has been added to the queue"
			def start = new Date(System.currentTimeMillis())
		//	render redirect (action: "waiting", params:[start: start, randomString: ""])
			def queueSize = Bcvjob.countByDateCreatedLessThanEquals(job.dateCreated) + Stapjob.countByDateCreatedLessThanEquals(job.dateCreated)
		
			if (queueSize > 2){  // 1 running task + our task
				render view: "waitinginqueue", model:[start:start, sessionId: job.sessionId, task:job.class, id:job.id, dateCreated:job.dateCreated]
				return
				//render "<p>${randomString}</p>"
				//sleep(5000)
				//queueSize = Bcvjob.countByDateCreatedLessThanEquals(job.dateCreated) + Stapjob.countByDateCreatedLessThanEquals(job.dateCreated)
			}
			
			else {
				run (job, jobService)
			}
		}
		
	}

	
	def runAsync (Object job, Object jobService){

			def GParsPool = new GParsPool()
			def pool = new ForkJoinPool(1)
			GParsPool.withExistingPool (pool, {
				jobService.getWaitingPipeline.callAsync(job)
				pool.shutdown()
			})

			render "Success! Your results will be sent at ${job.email}"

			
			//render view: "testview" // works here! wont work, if there is something else after this line. If one render goes after another - only the last one is rendered

	}

	
	def runner (){
		def job
		def jobService
		
		if (params.task == "class bcvapp.Bcvjob"){
			job = Bcvjob.get(params.id)
			jobService = bcvjobService
		}
		else if (params.task == "class bcvapp.Stapjob"){
			job = Stapjob.get(params.id)
			jobService = stapjobService
		}
		
		run (job, jobService)
		render "ok"
	}
	
	def run (Object job, Object jobService, Integer queueSize) {

		def GParsPool = new GParsPool()
		def pool = new ForkJoinPool(1)
				
		if (queueSize > 2){
			GParsPool.withExistingPool (pool, {
				jobService.getWaitingPipeline.callAsync(job)
				pool.shutdown()
			})
		}
		else {
			GParsPool.withExistingPool (pool, {
				jobService.getPipeline.callAsync(job)
				pool.shutdown()
			})
		}

		
		def start = new Date(System.currentTimeMillis())

		def resultsPath = jobService.getResults(job.sessionId)
		def zipResultsPath = jobService.getZipResults(job.sessionId)
		def path = resultsPath.split('/')
		def pathEnd = path[path.length-3] + "/" + path[path.length-2]
		def url = createLink(controller: 'job', action: 'renderResults', params: [pathEnd:pathEnd, sessionId:job.sessionId])
		render(contentType: 'text/html', text: "<script>window.location.href='$url'</script>")
		
		
		
//		while (!pool.isTerminated()){
//			def randomString = jobService.talkWork()
//			//murl = createLink(controller: 'job', action: 'waiting', params:[start:start, randomString: randomString])
//			//render(contentType: 'text/html', text: "<script>window.location.href='$murl'</script>")
//			render view: "waiting", model:[start:start, randomString: randomString]
//			//render "<p>${randomString}</p>"
//			sleep(5000)
//		}
//
//		def resultsPath = jobService.getResults(job.sessionId)
//		def zipResultsPath = jobService.getZipResults(job.sessionId)
//		def url = createLink(controller: 'job', action: 'renderResults', params: [resultsPath: resultsPath, zipResultsPath:zipResultsPath])
//		render(contentType: 'text/html', text: "<script>window.location.href='$url'</script>")

	}
	
	//@Synchronized("timeStampMap")
	def updateTimeStamp(){
		synchronized(timeStampMap){
			timeStampMap.putAt(params.sessionId, params.timeStamp)
			
		}	
		println " managed to put ${params.timeStamp} into map for ${params.sessionId}"
		def randomString = ""
		if (params.waitingType == "queue") {
			randomString = bcvjobService.talkQueue()
		}
		else {
			randomString = bcvjobService.talkWork()
		}
		render  "<p>${randomString}</p>"
	}
	

	def jobIsDone(){
		def isDone = holderService.isDone(params.sessionId)
		println("holderService holds " +isDone + " for " + params.sessionId)
		render "${isDone}"
	}
	
	def queueIsFinished(){
		def date = new Date().parse("yyyy-M-d H:m:s", params.dateCreated)

		def queueSize = Bcvjob.countByDateCreatedLessThanEquals(date) + Stapjob.countByDateCreatedLessThanEquals(date)
		println (" date " + date + ", queue size " + queueSize)
		if (queueSize > 2){
			render "false"
		}
		else render "true"
	}
	
	def waitingPage () {
		def start = new Date(System.currentTimeMillis())
		println ("going to render waiting page: sessionId ${params.sessionId}, task ${params.task}")
		render view: "waiting", model:[start: start, sessionId:params.sessionId, task:params.task]
	}
	
	
	def waiting = {
		
	}
	
	def showResultsPage(){
		def jobService
		if (params.task == "class bcvapp.Bcvjob"){
			jobService = bcvjobService
		}
		else if (params.task == "class bcvapp.Stapjob"){
			jobService = stapjobService
		}
		
		else {
			jobService = bcvjobService
		}
				def resultsPath = jobService.getResults(params.sessionId)
				def zipResultsPath = jobService.getZipResults(params.sessionId)
				def url = createLink(controller: 'job', action: 'renderResults', params: [resultsPath: resultsPath, zipResultsPath:zipResultsPath])
			//	render (contentType: 'text/html', text: "<script>window.location.href='$url'</script>")
				render "${url}"
	}

	//@Synchronized("timeStampMap")
	def Closure killIfAbandoned = {Object job ->
		def prev = 0L
		def now = 1L
		def sessionId = job.sessionId
		println ("killing feature for ${sessionId} is activated")
		println now.class.name
println ("prev " + prev)
println ("now " + now)
if (now > prev) {
	println ("bigger")
}

		while (now > prev){
			println " from the cycle"
			prev = now
			sleep(15000)
			println " going to take from map. my time is ${System.currentTimeMillis()}"
			synchronized(timeStampMap){
			//println timeStampMap.getAt(sessionId).class.name
			//println timeStampMap.getAt(sessionId)
			//println timeStampMap.getAt(sessionId).toLong()
			now = timeStampMap.getAt(sessionId).toLong()
			}
			println (" step3: prev " +  prev + ", now " + now + " sessionId " + sessionId)
		}

		println ("killing feature broke out of the cycle " + sessionId)
		if ((Bcvjob.findBySessionId(sessionId)||Stapjob.findBySessionId(sessionId))&& !job.email){
			println ("trying to stop " + sessionId) 
			def deleted = holderService.stopPipeline(sessionId)
			if (!deleted){ // otherwise it must be deleted by getPipeline itself
				job.delete(flush:true) 
			}
		}
	}

}




