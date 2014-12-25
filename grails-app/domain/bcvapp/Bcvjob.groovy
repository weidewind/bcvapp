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
		//	distance (min: 0f, max: 0.1f)
			distance (nullable:true)
			distance validator: { value, bcvjob, errors ->
				if ((value && value >= 0f && value <= 0.1f) || bcvjob.isExample == "true"){
					return true
				}
				errors.rejectValue( "distance", "bcvjob.distance.badvalue", "Distance is null or out of range.")
				return false
			}
			taxdb (nullable:true)
			taxdb validator: { value, bcvjob ->
				if (value || bcvjob.isExample == "true"){
					return true
				}
				errors.rejectValue( "taxdb", "bcvjob.taxdb.badvalue", "taxdb is null.")
				return false
			}
			deletedFiles (nullable: true)
			exampleFileName(nullable: true)
			files(nullable: true)
			exampleFilesNumber (blank:true, nullable: true)
			files validator: { value, bcvjob, errors ->
				if (value ||  bcvjob.isExample == "true"){
					return true
				}
				errors.rejectValue( "files", "bcvjob.files.badvalue", "No input files selected")
				return false
			}
	
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
