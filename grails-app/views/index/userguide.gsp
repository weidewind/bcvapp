
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
  <meta name="description" content="BCV project homepage" />
  <meta name="keywords" content="DNA, sequencing, basecaller, chromatogram, mixture, deconvolution, Sanger, HMM" />
  <meta name="owner" content="favorov@sensi.org" />
  		<link rel="stylesheet" type="text/css" href="<g:createLinkTo dir='stylesheets' file='snazzy.css' /> " />
  		<link rel="shortcut icon" href="<g:createLinkTo dir='images', file='favicon.ico' />" type="image/x-icon" /> 
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
<tmpl:menu />

<h2>Documentation and samples</h2>
<ul>
<li>
<!--<a href="${createLink(controller: 'job', action: 'downloadFile' , params: [path: '/store/home/bcvapp/bcvapp/web-app/pipeline/bcv-userguide.odt', contentType: 'application/vnd.oasis.opendocument.text', filename: 'bcv-userguide.odt'])}">BCV manual</a><a href="bcv-userguide.odt"></a>-->
<a href="http://basecv.sourceforge.net/bcv-userguide.odt">BCV manual</a>
<p></p>
</li>
<li>
<a href="http://basecv.sourceforge.net/bcv-examples.tar.gz">BCV usage examples</a>
<p>To run the examples, first set <tt>BCV_HOME</tt> variable to point to the <em>bcvhome</em> directory in the unpacked archive:
<pre>
     export BCV_HOME=<em>/path/to/examples/bcvhome</em>
</pre>
in addition to the settings described in installation section.</p>
</li>
<li>
todo: BCV online output format explanation 
</li>
</ul>
</body>
</html>

