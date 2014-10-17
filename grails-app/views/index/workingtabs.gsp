<!DOCTYPE html
	PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en-US" xml:lang="en-US">
	<head>
		<title>bcv-pipeline</title>

		<link rel="stylesheet" type="text/css" href="<g:createLinkTo dir='css' file='snazzy.css' /> " />
		<script type="text/javascript" src="<g:createLinkTo dir='javascripts' file='jquery-1.11.1.min.js' />"></script> 
		<script type="text/javascript" src="<g:createLinkTo dir='javascripts' file='jquery-ui.js' />"></script> 
		<link rel="stylesheet" type="text/css" href="<g:createLinkTo dir='css' file='jquery-ui.css' /> "> 
		<input type="hidden" name="tasktype" value="bcvstap">
		
		<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
		
		<!-- Google Analytics -->
		<script>
		(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
		(i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
		m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
		})(window,document,'script','//www.google-analytics.com/analytics.js','ga');
		
		ga('create', 'UA-XXXX-Y', 'auto');
		ga('send', 'pageview');

		</script>
		<!-- End Google Analytics -->
	</head>
	<body>

		<div id="tabs">
 			<ul>
 				<li><a href="${createLink(controller: 'index', action: 'index')}">Home</a></li>
 				<li><a href="installguide.gsp">Download&Install</a></li>
 				<li><a href="userguide.gsp">User guide</a></li>
 			<!--		<li><a href="${createLink(controller: 'index', action: 'installguide')}">Download&Install</a></li> -->
   			<!--		<li><a href="${createLink(controller: 'index', action: 'userguide')}">User Guide</a></li> -->
    		<!--	<li><a href="${createLink(controller: 'bcvjob', action: 'form')}">BCV online</a></li> -->
   			<!--	<li><a href="${createLink(controller: 'stapjob', action: 'stapform')}">STAP identification</a></li> -->
   		
   			<li><a href="../bcvjob/form.gsp">BCV online</a></li> 
   			<li><a href="../stapjob/stapform.gsp">STAP identification</a></li> 
   			
   			<!-- <li> <g:remoteLink controller='bcvjob', action='form'> BCV </g:remoteLink></li> -->
   			</ul>	
   		</div>	

	
	
	
			<nav class="navbar navbar-default navbar-fixed-top">
  <div class="container-fluid">
    <div class="navbar-header">
      <a class="navbar-brand" href="#">BaseCV</a>
    </div>
    <div>
      <ul class="nav navbar-nav">
        <li class="active"><a href="${createLink(controller: 'index', action: 'index')}">Home</a></li>
        <li><a href="${createLink(controller: 'bcvjob', action: 'form')}">BCV online</a></li>
        <li><a href="#">Page 2</a></li> 
        <li><a href="#">Page 3</a></li> 
      </ul>
    </div>
  </div>
</nav>
	
	
	
	
	
		
		<script>
		

		 $(function() {
    $( "#tabs" ).tabs();
  });
			
		</script>
	</body>
</html>























