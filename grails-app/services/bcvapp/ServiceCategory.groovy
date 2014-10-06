package bcvapp

import grails.transaction.Transactional
import groovy.lang.Closure;

import java.awt.event.ItemEvent;
import java.util.regex.Pattern;


@Category(Object)
class ServiceCategory {
	
	def createSessionId() {
		def randomNum = new Random().nextInt(100000)
		def currentTime = System.currentTimeMillis()
		return "${randomNum}_${currentTime}"
	}
	
	
	
	def getResults (String sessionId){
		def resultsPath = "${absPath}${sessionId}" + "/simple_results.html"
		return resultsPath

	}
	
	
	def talkToUser(){
		def Random random = new Random()
		def lines = [
			"Spinning up the hamster...",
			"Shovelling coal into the server...",
			"Programming the flux capacitor",
			"Checking the gravitational constant in your locale",
			"Please wait. The server is powered by a lemon and two electrodes"
		]
		return lines[random.nextInt(lines.size())]
		
	}
	

	
}