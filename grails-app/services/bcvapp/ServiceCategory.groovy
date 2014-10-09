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
	
	
	

	
	
	def talkToUser(boolean queued){
		
		def Random random = new Random()
		def lines
		if (queued == false){
		    lines = [
			"Spinning up the hamster...",
			"Shovelling coal into the server...",
			"Programming the flux capacitor",
			"Checking the gravitational constant in your locale",
			"Please wait. The server is powered by a lemon and two electrodes"
		]
		}
		else {
			lines = [
				"An Englishman, even if he is alone, forms an orderly queue of one. (George Mikes)",
				"How about making yourself a cup of tea and reading a book? This could take some time."
			]
		}
		return lines[random.nextInt(lines.size())]
		
	}
	
	def talkToUser(){
		return talkToUser(false)
	}
	
	

	
}