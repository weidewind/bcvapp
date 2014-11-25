package bcvapp

import grails.transaction.Transactional
import groovy.transform.Synchronized;

@Transactional
class HolderService {

	final def jobDone = [:].asSynchronized()
	final def procs = [:].asSynchronized()
	
	// @Synchronized("jobDone")
	 def setDone(String sessionId) {
		println ( sessionId + " is already done ")
		synchronized(jobDone){
		jobDone.putAt(sessionId, true)
		}
    }
	
	//@Synchronized("jobDone")
	def isDone(String sessionId) {
		synchronized(jobDone){
			def isDone = jobDone.get(sessionId)
			if (isDone){
				jobDone.delete(sessionId)
			}
		}
		return isDone
	}
	
	def boolean stopPipeline(String sessionId){
		if (procs[sessionId]){
			procs[sessionId].out.close()
			procs[sessionId].err.close()
			procs[sessionId].waitForOrKill(3000)
			procs.delete(sessionId)
			return true;
		}
		return false;
	}
}
