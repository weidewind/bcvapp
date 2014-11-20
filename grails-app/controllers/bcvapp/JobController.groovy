package bcvapp

import groovyx.gpars.GParsPool
import jsr166y.ForkJoinPool
import java.util.concurrent.Future
import groovy.lang.Closure;


class JobController {

	def bcvjobService
	def stapjobService
	def holderService
	
	def timeStampMap = [:]



	def index = {
		//redirect(action: form)
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
		job.setSessionId(sessionId)

		// Get files and directions

		def fileList = request.getFiles('files')
		def  directionList = []
		for (int i = 0; i < fileList.size(); i++){
			directionList.add(params.('checkbox' + i))
		}

		def errorMessage = jobService.checkInput(job, fileList)

		if (errorMessage) {
			render "${errorMessage}"
			return
		}
		
		if (!params.checkbox_email){
			job.setEmail("")
		}
		
		
		job.save()
		jobService.prepareDirectory(job, sessionId, fileList, directionList)

		def queueSize = Bcvjob.countByDateCreatedLessThanEquals(job.dateCreated) + Stapjob.countByDateCreatedLessThanEquals(job.dateCreated)

		
		
		if (job.email){
			runAsync(job, jobService)
		}
		else {
			if(queueSize > 2){		
					render redirect (action: "askforemail", id: job.id, params:[task:job.class])
					return
			}

		else run (job, jobService)	
		}


	}

	def renderResults (String resultsPath, String zipResultsPath){

		def htmlContent = new File(resultsPath).text
		def matcher = (htmlContent =~ /<a href.*?<br>/);
		htmlContent = matcher.replaceAll("");
		matcher = (htmlContent =~ /Length: *?nt/);
		htmlContent = matcher.replaceAll("");
		render ("<a href='${createLink(action: 'downloadFile' , params: [path: zipResultsPath, contentType: 'application/zip', filename: 'results.zip'])}'>Download all files</a> (.fasta files, trees and the report itself) <p></p>")
		render (text: htmlContent, contentType:"text/html", encoding:"UTF-8")
		
		
	
//		def htmlContent = new File(resultsPath).text
//		render ("<a href='${createLink(action: 'downloadFile' , params: [zipResultsPath: zipResultsPath])}'>Download all files</a> (.fasta files, trees and the report itself) <p></p>")
//		
//		render (text: htmlContent, contentType:"text/html", encoding:"UTF-8")
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
			println('Canceled download?', e)
		} finally {
			if (outputStream != null){
				try {
					outputStream.close()
				} catch (IOException e) {
					println('Exception on close', e)
				}
			}
		}

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
			render redirect (action: "waiting", params:[start: start, randomString: ""])
			def queueSize = Bcvjob.countByDateCreatedLessThanEquals(job.dateCreated) + Stapjob.countByDateCreatedLessThanEquals(job.dateCreated)
			while (queueSize > 2){  // 1 running task + our task
				def randomString = jobService.talkQueue()
				 render redirect (action: "waiting", params:[start: start, randomString: randomString])
				//render "<p>${randomString}</p>"
				sleep(5000)
				queueSize = Bcvjob.countByDateCreatedLessThanEquals(job.dateCreated) + Stapjob.countByDateCreatedLessThanEquals(job.dateCreated)
			}
			
			run (job, jobService)
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

	
	def run (Object job, Object jobService) {

		def GParsPool = new GParsPool()
		def pool = new ForkJoinPool(1)
		GParsPool.withExistingPool (pool, {
			jobService.getPipeline.callAsync(job)
			pool.shutdown()
			
		})
		def start = new Date(System.currentTimeMillis())
		//def murl = createLink(controller: 'job', action: 'waiting', params:[start:start])
		//render(contentType: 'text/html', text: "<script>window.location.href='$murl'</script>")
		render view: "waiting", model:[start:start, sessionId: job.sessionId, task:job.class]
		//render "<p>Please, don't close this page. Your task was submitted at ${start}.</p>"
		
		
		
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
	
	def updateTimeStamp(){
		timeStampMap.putAt(params.sessionId, params.timeStamp)
		println("timestamp " + params.timeStamp)
		def randomString = bcvjobService.talkWork()
		render  "<p>${randomString}</p>"
	}
	
	
	def jobIsDone(){
		def isDone = holderService.isDone(params.sessionId)
		println("holderService holds " +isDone + " for " + params.sessionId)
		render "${isDone}"
	}
	
	def showResultsPage(){
		def jobService
		if (params.task == "class bcvapp.Bcvjob"){
			jobService = bcvjobService
		}
		else if (params.task == "class bcvapp.Stapjob"){
			jobService = stapjobService
		}
				def resultsPath = jobService.getResults(params.sessionId)
				def zipResultsPath = jobService.getZipResults(params.sessionId)
				render redirect (controller: 'job', action: 'renderResults', params: [resultsPath: resultsPath, zipResultsPath:zipResultsPath])
			//	render (contentType: 'text/html', text: "<script>window.location.href='$url'</script>")
	}


}




