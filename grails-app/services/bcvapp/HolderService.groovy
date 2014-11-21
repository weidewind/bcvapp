package bcvapp

import grails.transaction.Transactional
import groovy.transform.Synchronized;

@Transactional
class HolderService {

	final def jobDone = [:]
	final def procs = [:]
	
	 @Synchronized("jobDone")
	 def setDone(String sessionId) {
		println ( sessionId + " is already done ")
		jobDone.putAt(sessionId, true)
    }
	
	@Synchronized("jobDone")
	def isDone(String sessionId) {
		//println ("Checking if " + sessionId + " done .. " + jobDone.get(sessionId))
		return jobDone.get(sessionId)
	}
	
	def stopPipeline(String sessionId){
		procs[sessionId].out.close()
		procs[sessionId].err.close()
		procs[sessionId].waitForOrKill(3000)
	}
}
