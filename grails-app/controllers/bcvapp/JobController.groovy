package bcvapp

import groovyx.gpars.GParsPool
import jsr166y.ForkJoinPool
import java.util.concurrent.Future
import groovy.lang.Closure;


//import groovyx.gpars.*

class JobController {

	def bcvjobService
	def stapjobService


	def index = {
		//redirect(action: form)
	}

	//	def form =  {
	//
	//	}
	//
	//	def stapform =  {
	//	}

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

		job.save()

		jobService.prepareDirectory(job, sessionId, fileList, directionList)
	//	Closure pipeline = jobService.getPipeline(job)

		def queueSize = Bcvjob.countByDateCreatedLessThanEquals(job.dateCreated) + Stapjob.countByDateCreatedLessThanEquals(job.dateCreated)
		

		//	render jobService.getAbsPath()

		if (job.email){

			runAsync(job, jobService)

		}
		else {
			if(queueSize > 2){
				
					render redirect (action: "askforemail", id: job.id, params:[task:job.class])
					return
				
//				render "Your task has been added to the queue"
//				while (queueSize > 2){  // 1 running task + our task
//					def randomString = jobService.talkQueue()
//					render "<p>${randomString}</p>"
//					sleep(5000)
//					queueSize = Bcvjob.countByDateCreatedLessThanEquals(job.dateCreated) + Stapjob.countByDateCreatedLessThanEquals(job.dateCreated)
//				}
			}
			//render "Wait here. Your sessionId is ${sessionId}"
			
			// now in run
			
//			def GParsPool = new GParsPool()
//			def pool = new ForkJoinPool(1)
//			GParsPool.withExistingPool (pool, {
//				pipeline.callAsync(sessionId, null)
//				pool.shutdown()
//			})
//			def start = new Date(System.currentTimeMillis())
//			render "<p>Please, don't close this page. Your task was submitted at ${start}.</p>"
//			while (!pool.isTerminated()){
//				def randomString = jobService.talkWork()
//				render "<p>${randomString}</p>"
//				sleep(5000)
//			}
//
//			def resultsPath = jobService.getResults(sessionId)
//			def url = createLink(controller: 'job', action: 'renderResults', params: [resultsPath: resultsPath])
//			render(contentType: 'text/html', text: "<script>window.location.href='$url'</script>")
//			//	job.delete(flush:true)
			
		else run (job, jobService)	
		}


	}

	//	def createSessionId() {
	//		def randomNum = new Random().nextInt(100000)
	//		def currentTime = System.currentTimeMillis()
	//		return "${randomNum}_${currentTime}"
	//	}
	//
	def renderResults (String resultsPath){
		def htmlContent = new File(resultsPath).text
		render ("<a href='${createLink(action: 'downloadFile' , params: [resultsPath: resultsPath])}'>Download report</a>")
		render (text: htmlContent, contentType:"text/html", encoding:"UTF-8")
	}

	def downloadFile(String resultsPath){
		def file = new File(resultsPath)
		response.setContentType("text/html")
		response.setHeader("Content-disposition", "filename=${myfile}")
		response.outputStream << file.getBytes()
		response.outputStream.flush()
	}
	
	def askforemail = {

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
			render "Your task has been added to the queue"
			def queueSize = Bcvjob.countByDateCreatedLessThanEquals(job.dateCreated) + Stapjob.countByDateCreatedLessThanEquals(job.dateCreated)
			
			while (queueSize > 2){  // 1 running task + our task
				def randomString = jobService.talkQueue()
				render "<p>${randomString}</p>"
				sleep(5000)
				queueSize = Bcvjob.countByDateCreatedLessThanEquals(job.dateCreated) + Stapjob.countByDateCreatedLessThanEquals(job.dateCreated)
			}
			
			run (job, jobService)
		}
		
	}

	
	def runAsync (Object job, Object jobService){
		//	Closure pipeline = jobService.getPipeline(job)
			def GParsPool = new GParsPool()
			def pool = new ForkJoinPool(1)
			GParsPool.withExistingPool (pool, {
				
	
//			def queueSize = Bcvjob.countByDateCreatedLessThanEquals(job.dateCreated) + Stapjob.countByDateCreatedLessThanEquals(job.dateCreated)
//				
//				if(queueSize > 2){
//					while (queueSize > 2){  // 1 running task + our task
//						sleep(5000)
//						queueSize = Bcvjob.countByDateCreatedLessThanEquals(job.dateCreated) + Stapjob.countByDateCreatedLessThanEquals(job.dateCreated)
//					}
//				}
			//	pipeline.callAsync(job.sessionId, job.email)
				jobService.getWaitingPipeline.callAsync(job)
			})


			render "Success! Your results will be sent at ${job.email} "
	}

	
	def run (Object job, Object jobService) {
		//Closure pipeline = jobService.getPipeline(job)
		def GParsPool = new GParsPool()
		def pool = new ForkJoinPool(1)
		GParsPool.withExistingPool (pool, {
			//pipeline.callAsync(job.sessionId, null)
			jobService.getPipeline.callAsync(job)
			pool.shutdown()
		})
		def start = new Date(System.currentTimeMillis())
		render "<p>Please, don't close this page. Your task was submitted at ${start}.</p>"
		while (!pool.isTerminated()){
			def randomString = jobService.talkWork()
			render "<p>${randomString}</p>"
			sleep(5000)
		}

		def resultsPath = jobService.getResults(job.sessionId)
		def url = createLink(controller: 'job', action: 'renderResults', params: [resultsPath: resultsPath])
		render(contentType: 'text/html', text: "<script>window.location.href='$url'</script>")
		//	job.delete(flush:true)
	}

	//
	//
	//
	//	def sendResults(String email, String resultsFilePath) {
	//		mailService.sendMail {
	//		multipart true
	//		to email
	//		subject "BCV results"
	//		body "Have a nice day!"
	//		attachBytes resultsFilePath,'text/xml', new File(resultsFilePath).readBytes()
	//
	//		}
	//
	//	}
	//
	//	def compute (String a){
	//		Thread.sleep (3000)
	//		return a
	//	}
	//
	//	def talkToUser(){
	//		def lines = [
	//			"Spinning up the hamster...",
	//			"Shovelling coal into the server...",
	//			"Programming the flux capacitor",
	//			"Checking the gravitational constant in your locale",
	//			"Please wait. The server is powered by a lemon and two electrodes"
	//		]
	//		return lines[random.nextInt(lines.size())]
	//
	//	}

	
//	def Closure waitInQueue = { job ->
//		def queueSize = Bcvjob.countByDateCreatedLessThanEquals(job.dateCreated) + Stapjob.countByDateCreatedLessThanEquals(job.dateCreated)
//		
//		if(queueSize > 2){
//			while (queueSize > 2){  // 1 running task + our task
//				sleep(5000)
//				queueSize = Bcvjob.countByDateCreatedLessThanEquals(job.dateCreated) + Stapjob.countByDateCreatedLessThanEquals(job.dateCreated)
//			}
//		}
//	}

}




