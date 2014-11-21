package bcvapp

import grails.transaction.Transactional

@Transactional
class HolderService {

	def jobDone = [:]
	def procs = [:]
	
    def setDone(String sessionId) {
		println ( sessionId + " is already done ")
		jobDone.putAt(sessionId, true)
    }
	
	def isDone(String sessionId) {
		println ("Checking if " + sessionId + " done .. " + jobDone.get(sessionId))
		return jobDone.get(sessionId)
	}
	
	def stopPipeline(String sessionId){
		procs[sessionId].out.close()
		procs[sessionId].err.close()
		procs[sessionId].waitForOrKill(3000)
	}
}
