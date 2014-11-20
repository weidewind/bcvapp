package bcvapp

import grails.transaction.Transactional

@Transactional
class HolderService {

	def jobDone = [:]
	
    def setDone(String sessionId) {
		println ( sessionId + " done ")
		jobDone.putAt(sessionId, true)
    }
	
	def isDone(String sessionId) {
		println ("Checking if " + sessionId + " done .. " + jobDone.get(sessionId))
		return jobDone.get(sessionId)
	}
}
