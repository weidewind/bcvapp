class MyTagLib {
	def redirectMainPage = {
	  response.sendRedirect("${request.contextPath}/bcvjob/form.gsp/")
	}
  }