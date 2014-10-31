<!DOCTYPE html
	PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en-US" xml:lang="en-US">
	<head>
		<title>bcv-pipeline</title>

		<link rel="stylesheet" type="text/css" href="<g:createLinkTo dir='stylesheets' file='snazzy.css' /> " /> 
	<!--		<script type="text/javascript" src="<g:createLinkTo dir='javascripts' file='jquery-1.11.1.min.js' />"></script> -->
		<link rel="shortcut icon" href="<g:createLinkTo dir='images', file='favicon.ico'>" type="image/x-icon" /> 
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
	

<tmpl:menu />

		<div class = "central-panel">
			<h1>Base Caller with Vocabulary</h1>
			
				<p>The Base Calling with Vocabulary (BCV) software package is intended for analysis of direct (population) sequencing chromatograms using known vocabulary sequences similar to the target DNA. The current version of BCV can only process chromatogram files obtained on Applied Biosystems capillary sequencing machines (ABI file format). In principle, the method can be applied to other sequencing platforms except high-throughput sequencing; however, the current software version has not been tested for such applications.</p>
				<p>The BCV package has the following functionalities:
				<ul>
					<li>Base calling: determining the sequence of IUPAC codes corresponding to sets of 1-4 nucleotides at each position for a chromatogram</li>
					<li>Indel detection: detecting insertions or deletions (indels) in components of the the DNA sample mixture  relative to the main consensus sequence of the major pool of DNA variants</li>
					<li>Mixture deconvolution: determining the sequences of the sample components</li>
				</ul>
				</p>
				<p>The BCV software is distributed under the GNU GPL v. 3 license, and was successfully compiled on Linux and Windows Cygwin platforms.</p>
			   	
			   	<p>To enjoy the full functionality of BCV, <a href="${createLinkTo(dir: '/index', file: 'installguide.gsp')}">download and install </a> it locally. </p>
			   	<p>For base calling, mixture deconvolution and taxonomic assignment of 16S rRNA mixed chromatograms, use <a href="${createLinkTo(dir: '/bcvjob', file: 'form.gsp')}">BCV online</a>. </p>  
			   	
			   	<div class="citation">
			    	<p> Citation: <a href="http://www.plosone.org/article/info%3Adoi%2F10.1371%2Fjournal.pone.0054835">Base-Calling Algorithm with Vocabulary (BCV) Method for Analyzing Population Sequencing Chromatograms</a> </p>
					<p>Fantin YS, Neverov AD, Favorov AV, Alvarez-Figueroa MV, Braslavskaya SI, et al. (2013) Base-Calling Algorithm with Vocabulary (BCV) Method for Analyzing Population Sequencing Chromatograms. PLoS ONE 8(1): e54835. doi: 10.1371/journal.pone.0054835 </p>
				</div>
			
  	<!--		<g:form controller = "index"> -->
  	 	<!--		<g:actionSubmit class = "myButton" value="BCV online" action = "bcvRedirect"/> -->
  	 	<!--		<g:actionSubmit class = "myButton" value="Download & install" action = "installRedirect"/> -->
  	 	<!--		<g:actionSubmit class = "myButton" value="User guide" action = "guideRedirect" /> -->

		 	<!--	</g:form> -->
		</div>
		
	</body>
</html>
		