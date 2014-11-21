<!DOCTYPE html
	PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en-US" xml:lang="en-US">
	<head>
		<title>bcv-pipeline</title>
			<link rel="stylesheet" type="text/css" href="<g:createLinkTo dir='css' file='snazzy.css' /> " />
			 <link rel="shortcut icon" href="<g:createLinkTo dir='images', file='favicon.ico' />" type="image/x-icon" /> 
		<script type="text/javascript" src="<g:createLinkTo dir='javascripts' file='jquery-1.11.1.min.js' />"></script>
		
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />

		</head>
	<body>
		
<p>Please, don't close this page. Your task was submitted at ${start}. The results will be shown here when they are ready. </p>
<div id="randomString">
        </div>
 <div id="jobDone" style="display: none;">
        </div>
 <div id="resultsUrl" style="display: none;">
        </div>


<g:javascript>


    var startTimeStamp = d.getTime();
    var interval = setInterval('checkAndUpdate("${sessionId}", "${task}")', '5000');

     function checkAndUpdate(sessionId, task){
     console.log(sessionId)
      	var d = new Date();
        timeStamp = d.getTime()-startTimeStamp;
        console.log(timeStamp);
        ${remoteFunction(controller: 'job', action: 'updateTimeStamp', update: 'randomString', params: '{timeStamp:timeStamp, sessionId:sessionId}')};
        ${remoteFunction(controller: 'job', action: 'jobIsDone', update: 'jobDone', params: '{sessionId:sessionId}')};
        var jobIsDone = document.getElementById('jobDone').innerHTML;
        if (jobIsDone === "true"){
        	${remoteFunction(controller: 'job', action: 'showResultsPage', update: 'resultsUrl', onSuccess:'loadResults(data);', params: '{task:task, sessionId:sessionId}' )};
        	clearInterval(interval);
        //	var url = "http://bcvapp.cmd.su:8080" + document.getElementById('resultsUrl').innerText;
        //	console.log(url)
        //	window.location.href = url;
        }
        
    }
    
function loadResults(data){
        	var url = "http://bcvapp.cmd.su:8080" + data;
        	console.log(url)
        	window.location.href = url;
}
    

</g:javascript>
</body>
</html>