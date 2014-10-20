package bcvapp

class IndexController {

    def index() {
	//	redirect(controller: "bcvjob", action: "form") 
		}
	
//	def bcvRedirect (){
//			 render redirect(controller: "bcvjob", action: "form")
//		}
	
		def bcvRedirect (){
				 render redirect(action: "tabs")
			}
	
	def guideRedirect (){
		render redirect (action: "userguide")
			//redirect(controller: "stapjob", action: "stapform")
		}
	
	def installRedirect (){
		render redirect (action: "installguide")
			//redirect(controller: "stapjob", action: "stapform")
		}
	
	def userguide = {
		
	}
	
	def installguide = {
	
	}

	
	def tabs = {
		
	}
}
