package bcvapp

import java.util.List;

class Stapjob {

String sequences
List files
String database
float distance
String email


    static constraints = {
		email (blank:true, nullable:true, email:true)
		distance (min: 0f, max: 0.1f)
		files (nullable: true)
		sequences (blank: true, nullable: true)

    }
}
