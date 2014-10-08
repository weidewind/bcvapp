package bcvapp

import groovyx.gpars.GParsPool
import jsr166y.ForkJoinPool
import java.util.concurrent.Future


//import groovyx.gpars.*

class BcvjobController {
	
	def scaffold = true
	def bcvjobService
	

	def index = {
	//	redirect(action: form)
		}
	
	def form =  {
		
	}
	
	
	def submit() {
		
		def job = new Bcvjob(params)
		
		//def sessionId1 = bcvjobService.createSessionId()
		def sessionId = "5"
		
		job.setSessionId(sessionId)
		
		
		// Get files and directions
		
		def fileList = request.getFiles('files')
		def  directionList = []
		for (int i = 0; i < fileList.size(); i++){
			directionList.add(params.('checkbox' + i))
		}
		
		def errorMessage = bcvjobService.checkInput(job, fileList)
		
		if (errorMessage) {
			render "${errorMessage}"
			return
		}
		
		job.save()
		
		
		def uploadPath = bcvjobService.prepareDirectory(job, sessionId, fileList, directionList)
		Closure pipeline = bcvjobService.getPipeline(job)
		
		def queueSize = Bcvjob.countByDateCreatedLessThanEquals(job.dateCreated) + Stapjob.countByDateCreatedLessThanEquals(job.dateCreated)
		
		
		render bcvjobService.getAbsPath()

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
					def randomString = bcvjobService.talkToUser(true)
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
				def randomString = bcvjobService.talkToUser()
				render "<p>${randomString}</p>"
				sleep(5000)
			}

			def url = createLink(controller: 'bcvjob', action: 'renderResults', params: [sessionId: sessionId])
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
	def renderResults (String sessionId){
		def htmlContent = new File(bcvjobService.getResults(sessionId)).text
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
	
	Closure myExpensiveCalculation = {
		sleep (5000)
		mailService.sendMail {
			multipart true
			to "weidewind@gmail.com"
			subject "BCV results"
			body "Have a nice day!"
			attachBytes "C:/Users/weidewind/Documents/CMD/website/bcvrun.prj.xml",'text/xml', new File("C:/Users/weidewind/Documents/CMD/website/bcvrun.prj.xml").readBytes()
			
			}
	}
	
	Closure myTestRun = { sessionId, email ->

		sleep (7000)
		bcvjobService.runPipeline(sessionId)
		if (email != null) {
			bcvjobService.sendResults(email, "5")
		}

	}
	
	Closure checkLogs = { sessionId ->
		
		
		
	}
}
	
	


