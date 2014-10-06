package bcvapp

import java.util.List;

class Bcvjob {
	
	List files
	String database
	String vocabulary
	float distance
	String email
	List checkbox
	
	
		static constraints = {
			email (blank:true, nullable:true, email:true)
			distance (min: 0f, max: 0.1f)
	
		}

}
