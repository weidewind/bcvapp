package bcvapp

import grails.transaction.Transactional
import groovy.transform.Synchronized
import java.security.MessageDigest

@Transactional
class HolderService {

	final def jobDone = [:].asSynchronized()
	final def procs = [:].asSynchronized()
	final def cheksums = [] as Queue
	final def maxCheksumsSize = 15
	
	// @Synchronized("jobDone")
	 def setDone(String sessionId) {
		println ( sessionId + " is already done ")
		synchronized(jobDone){
		jobDone.putAt(sessionId, true)
		}
    }
	
	//@Synchronized("jobDone")
	def isDone(String sessionId) {
		def isDone
		synchronized(jobDone){
			isDone = jobDone.get(sessionId)
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
			return true
		}
		return false
	}
	
	def check(){
		
	}
	
	def generateMD5(String s) {
		MessageDigest digest = MessageDigest.getInstance("MD5")
		digest.update(s.bytes);
		new BigInteger(1, digest.digest()).toString(16).padLeft(32, '0')
	 }
	

}
