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
<p>${params.randomString}</p>


<g:javascript>


    var timeStamp = null;
    var interval = setInterval('checkAndUpdate(${sessionId})', '5000');

     function checkAndUpdate(sessionId){
      	var d = new Date();
        timeStamp = d.getTime();
        var newData = ${remoteFunction(controller: 'job', action: 'updateTimeStamp', params: '[timeStamp]')};
        var jobIsDone = ${remoteFunction(controller: 'job', action: 'jobIsDone', params: '[sessionId]')};
        if (jobIsDone){
        	${remoteFunction(controller: 'stapjob', action: 'stapform')};
        	clearInterval(interval);
        }
        
    }
    

    

</g:javascript>
</body>
</html>