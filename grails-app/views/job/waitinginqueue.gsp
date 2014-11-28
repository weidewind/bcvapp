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
		<!-- Google Analytics -->
		<script>
  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

  ga('create', 'UA-39054482-3', 'auto');
  ga('send', 'pageview');

</script>
		<!-- End Google Analytics -->
		</head>
	<body>
		
<p>Please, don't close this page. Your task was submitted at ${start} and now is waiting in queue. The results will be shown here when they are ready. </p>
<div id="randomString">
        </div>
 <div id="queueIsFinished" style="display: none;">
        </div>
        
  <div id="waiting" style="display: none;">
        </div>       


<g:javascript>

	var d = new Date();
    var interval = setInterval('checkAndUpdate("${sessionId}", "${task}", "${id}", "${dateCreated}")', '5000');

     function checkAndUpdate(sessionId, task, id, dateCreated){
     console.log(sessionId)
      	d = new Date();
        timeStamp = d.getTime()+100000;
        console.log(timeStamp);
        ${remoteFunction(controller: 'job', action: 'updateTimeStamp', update: 'randomString', params: '{timeStamp:timeStamp, sessionId:sessionId, waitingType:"queue"}')};
        ${remoteFunction(controller: 'job', action: 'queueIsFinished', update: 'queueIsFinished', params: '{dateCreated:dateCreated}')};
        var queueIsFinished = document.getElementById('queueIsFinished').innerHTML;
        if (queueIsFinished === "true"){
        console.log("finished waiting");
        	${remoteFunction(controller: 'job', action: 'runner', update: 'waiting', onSuccess:'loadWaiting(data, task, sessionId);', params: '{task:task, id:id}' )};
        	console.log("send call");
        	clearInterval(interval);
        	
        }
        
    }
    
    function loadWaiting(data, task, sessionId){
    console.log("will try to redirect");
    console.log ("parameter task" + task + " parameter sessionId" + sessionId);
     console.log("${createLink (controller:'job', action:'waitingPage', params:'{task:task, sessionId:sessionId}')}");
               	window.location.href = "${createLink (controller:'job', action:'waitingPage', params:[task:task, sessionId:sessionId])}";

}
    

</g:javascript>
</body>
</html>