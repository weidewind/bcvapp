
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
  <meta name="description" content="BCV project homepage" />
  <meta name="keywords" content="DNA, sequencing, basecaller, chromatogram, mixture, deconvolution, Sanger, HMM, BCV" />
  <meta name="owner" content="favorov@sensi.org" />
  		<link rel="stylesheet" type="text/css" href="<g:createLinkTo dir='stylesheets' file='snazzy.css' /> " />
  		<link rel="shortcut icon" href="<g:createLinkTo dir='images', file='myfavicon.ico' />" type="image/x-icon" /> 
  <title>Base Caller with Vocabulary (BCV)</title>
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
<div class = 'wrapper'>
<tmpl:menu />

<h2>Documentation and samples</h2>
<ul>
<li>
BCVapp (online version) output format explanation: <a href="http://basecv.sourceforge.net/BCVapp_presentation_rus.pdf">RUS</a>, <a href="http://basecv.sourceforge.net/BCVapp_presentation_eng.pdf">ENG</a>. 
<p></p>
</li>
<li>
BCVapp usage example: <a href="http://basecv.sourceforge.net/BCVapp_presentation_rus.pdf">RUS</a>, <a href="http://basecv.sourceforge.net/BCVapp_presentation_eng.pdf">ENG</a>. 
<p></p>
</li>
<li>
<!--<a href="${createLink(controller: 'job', action: 'downloadFile' , params: [path: '/store/home/bcvapp/bcvapp/web-app/pipeline/bcv-userguide.odt', contentType: 'application/vnd.oasis.opendocument.text', filename: 'bcv-userguide.odt'])}">BCV manual</a><a href="bcv-userguide.odt"></a>-->
<a href="http://basecv.sourceforge.net/bcv-userguide.odt">BCV manual</a>: here you can find a brief algorithm overview and a detailed manual for the standalone version.
<p></p>
</li>
<li>
<a href="http://basecv.sourceforge.net/bcv-examples.tar.gz">BCV usage examples</a>: sample vocabularies and chromatograms for the standalone version.
<p>To run the examples, first set <tt>BCV_HOME</tt> variable to point to the <em>bcvhome</em> directory in the unpacked archive:
<pre>
     export BCV_HOME=<em>/path/to/examples/bcvhome</em>
</pre>
in addition to the settings described in installation section.</p>
</li>

</ul>
<div class ="push"></div>
    </div>
    <tmpl:footer />
</body>
</html>

