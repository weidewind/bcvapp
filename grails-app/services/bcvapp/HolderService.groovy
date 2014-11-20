package bcvapp

import grails.transaction.Transactional

@Transactional
class HolderService {

	def jobDone = [:]
	
    def setDone(String sessionId) {
		jobDone.putAt(sessionId, true)
    }
	
	def isDone(String sessionId) {
		return jobDone.get(sessionId)
	}
}
