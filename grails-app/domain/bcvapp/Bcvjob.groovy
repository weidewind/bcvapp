package bcvapp

import java.util.List;

class Bcvjob {
	
	String sessionId
	List files
	String taxdb
	String vocabulary
	float distance
	String email
	List checkbox
	Date dateCreated
	
	
		static constraints = {
			email (blank:true, nullable:true, email:true)
			distance (min: 0f, max: 0.1f)
	
		}
		
		static mapping = {

			sessionId column: 'sessionId'
		}
		
		def setSessionId(String sessionId){
			this.sessionId = sessionId
		}

}
