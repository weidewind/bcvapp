<!DOCTYPE html
	PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en-US" xml:lang="en-US">
	<head>
		<title>bcv-pipeline</title>
			<link rel="stylesheet" type="text/css" href="<g:createLinkTo dir='css' file='snazzy.css' /> " />
		<script type="text/javascript" src="<g:createLinkTo dir='javascripts' file='jquery-1.11.1.min.js' />"></script>
		
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />

		</head>
	<body>
		
<p>There are several tasks already in the queue. Are you sure you don't want the results to be sent to your e-mail?</p>
<g:form controller = "job"  action = "updateAndRun"   onsubmit= "return validateForm(this.submited)">
<p><input type='text' name='email' id='email' size='50' maxlength='80' />
<div class = 'error'><label id = 'email_error'></label></div><p>
<g:hiddenField name="id" id = "id" value="${params.id}"/>
<g:hiddenField name="task" id = "task" value="${params.task}"/>
<g:submitButton class = "myButton" name = "wait" value="I will wait here" onclick="this.form.submited=this.name;"/>
<g:submitButton class = "myButton" name = "send"  value="OK, send them" onclick="this.form.submited=this.name;"/>

</g:form>

<script>

function validateForm(c) { 

			if (c === "wait") {
				document.getElementById("email_error").innerHTML = "";
				return true;
			}
			else if (c === "send"){
			var x = document.getElementById("email").value;
			var valid = true;
			var atpos = x.indexOf("@");
			var dotpos = x.lastIndexOf(".");
			if (atpos< 1 || dotpos<atpos+2 || dotpos+2>=x.length) {
				valid = false;
			}
			if (!valid){
				document.getElementById("email_error").innerHTML = "Invalid e-mail";
			}
			else {
				document.getElementById("email_error").innerHTML = "";
			}
			return valid;
			}
			else {
			return false;
			}
}


//window.onbeforeunload = function () {
// var message = 'Are you sure you want to leave?'; 
   // var e = e || <span class="skimlinks-unlinked">window.event</span>;
    // For IE and Firefox prior to version 4
   // ${remoteFunction(controller: 'job', action: 'deleteJob', params: '[id:${params.id}, task:${params.task}]')};
  //  if (e) {
   //     e.returnValue = message ;
   // }
    // For Safari
  //  return message;
 
/}

$(window).on('beforeunload', function() {
  return 'Your own message goes here...';
});

</script>
</body>
</html>