package bcvapp

import java.util.Date;
import java.util.List;

class Stapjob {

String sessionId
String sequences
List files
String taxdb
float distance
String email
String deletedFiles
Date dateCreated


    static constraints = {
		email (blank:true, nullable:true, email:true)
		distance (min: 0f, max: 0.1f)
		files (nullable: true)
		sequences (blank: true, nullable: true)
		deletedFiles (nullable: true)

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
