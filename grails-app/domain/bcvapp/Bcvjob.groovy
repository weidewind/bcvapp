package bcvapp

import java.util.List;

class Bcvjob {
	
	String sessionId
	List files
	String taxdb
	String vocabulary
	float distance
	String email
	List radioF
	String deletedFiles
	List exampleFileName
	int exampleFilesNumber
	String isExample
	Date dateCreated
	
	
		static constraints = {
			email (blank:true, nullable:true, email:true)
			distance (min: 0f, max: 0.1f)
			deletedFiles (nullable: true)
			exampleFileName(nullable: true)
			files(nullable: true)
			exampleFilesNumber (nullable: true)
	
		}
		
		static mapping = {

			sessionId column: 'sessionId'
		}
		
		def setSessionId(String sessionId){
			this.sessionId = sessionId
		}
		
		def setEmail(String email){
			this.email = email
		}

}
