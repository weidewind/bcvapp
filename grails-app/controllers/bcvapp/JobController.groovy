package bcvapp

import groovyx.gpars.GParsPool
import jsr166y.ForkJoinPool
import java.util.concurrent.Future


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


		//def sessionId1 = bcvjobService.createSessionId()
		def sessionId = "5"

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


		def uploadPath = jobService.prepareDirectory(job, sessionId, fileList, directionList)
		Closure pipeline = jobService.getPipeline(job)

		def queueSize = Bcvjob.countByDateCreatedLessThanEquals(job.dateCreated) + Stapjob.countByDateCreatedLessThanEquals(job.dateCreated)


		//	render jobService.getAbsPath()

		if (job.email != null){

			def GParsPool = new GParsPool()
			def pool = new ForkJoinPool(1)
			GParsPool.withExistingPool (pool, {

				if(queueSize > 2){
					while (queueSize > 2){  // 1 running task + our task
						sleep(5000)
						queueSize = Bcvjob.countByDateCreatedLessThanEquals(job.dateCreated) + Stapjob.countByDateCreatedLessThanEquals(job.dateCreated)
					}
				}
				pipeline.callAsync(sessionId, job.email)
			})


			render "Success! We will send your results at ${job.email} in about 30 minutes"

		}
		else {
			if(queueSize > 2){
				render "Your task has been added to the queue"
				while (queueSize > 2){  // 1 running task + our task
					def randomString = jobService.talkToUser(true)
					render "<p>${randomString}</p>"
					sleep(5000)
					queueSize = Bcvjob.countByDateCreatedLessThanEquals(job.dateCreated) + Stapjob.countByDateCreatedLessThanEquals(job.dateCreated)
				}
			}
			//render "Wait here. Your sessionId is ${sessionId}"
			def GParsPool = new GParsPool()
			def pool = new ForkJoinPool(1)
			GParsPool.withExistingPool (pool, {
				pipeline.callAsync(sessionId, null)
				pool.shutdown()
			})
			def start = new Date(System.currentTimeMillis())
			render "<p>Please, don't close this page. Your task was submitted at ${start}.</p>"
			while (!pool.isTerminated()){
				def randomString = jobService.talkToUser()
				render "<p>${randomString}</p>"
				sleep(5000)
			}
			
			def resultsPath = jobService.getResults(sessionId)
			def url = createLink(controller: 'job', action: 'renderResults', params: [resultsPath: resultsPath])
			render(contentType: 'text/html', text: "<script>window.location.href='$url'</script>")
			//	job.delete(flush:true)
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
		render (text: htmlContent, contentType:"text/html", encoding:"UTF-8")
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


}




