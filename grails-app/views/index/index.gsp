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
		<div class = "central-panel">
			<h1>Base Caller with Vocabulary</h1>
			<div class = "column">
				<p>The Base Calling with Vocabulary (BCV) software package is intended for analysis of direct (population) sequencing chromatograms using known vocabulary sequences similar to the target DNA. The current version of BCV can only process chromatogram files obtained on Applied Biosystems capillary sequencing machines (ABI file format). In principle, the method can be applied to other sequencing platforms except high-throughput sequencing; however, the current software version has not been tested for such applications.</p>
				<p>The BCV package has the following functionalities:
				<ul>
					<li>Base calling: determining the sequence of IUPAC codes corresponding to sets of 1-4 nucleotides at each position for a chromatogram</li>
					<li>Indel detection: detecting insertions or deletions (indels) in components of the the DNA sample mixture  relative to the main consensus sequence of the major pool of DNA variants</li>
					<li>Mixture deconvolution: determining the sequences of the sample components</li>
				</ul>
				</p>
				<p>The BCV software is distributed under the GNU GPL v. 3 license, and was successfully compiled on Linux and Windows Cygwin platforms.</p>
			</div>
  			<g:form controller = "index">
  			<g:actionSubmit class = "myButton" value="BCV online" action = "bcvRedirect"/>
  			<g:actionSubmit class = "myButton" value="Download & install" action = "installRedirect"/>
  			<g:actionSubmit class = "myButton" value="User guide" action = "guideRedirect" />

			</g:form> 
		</div>
		
	</body>
</html>
		