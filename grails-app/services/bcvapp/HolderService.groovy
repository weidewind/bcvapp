package bcvapp

import grails.transaction.Transactional
import groovy.transform.Synchronized

@Transactional
class HolderService {

	final def jobDone = [:].asSynchronized()
	final def procs = [:].asSynchronized()
	final def cheksums = [] as Queue
	final def maxCheksumsSize = 15
	

	 def setDone(String sessionId) {
		//println ( sessionId + " is already done ")
		synchronized(jobDone){
		jobDone.putAt(sessionId, true)
		}
    }
	

	def isDone(String sessionId) {
		def isDone
		synchronized(jobDone){
			isDone = jobDone.get(sessionId)
			if (isDone){
				jobDone.remove(sessionId)
			}
		}
		return 	isDone
	}
	
	def boolean stopPipeline(String sessionId){
		synchronized(procs){
			if (procs[sessionId]){
				procs[sessionId].out.close()
				procs[sessionId].err.close()
				procs[sessionId].waitForOrKill(3000)
				return true
			}
		}
		return false
	}
	
	def deleteProc (String sessionId){
		synchronized(procs){
				procs.remove(sessionId)
		}
	}
	

	

}
