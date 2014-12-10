<!DOCTYPE html
	PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en-US" xml:lang="en-US">
	<head>
		<title>bcv-pipeline</title>

		<link rel="stylesheet" type="text/css" href="<g:createLinkTo dir='stylesheets' file='snazzy.css' /> " />
		<link rel="shortcut icon" href="<g:createLinkTo dir='images', file='myfavicon.ico' />" type="image/x-icon" /> 
		<script type="text/javascript" src="<g:createLinkTo dir='javascripts' file='jquery-1.11.1.min.js' />"></script>
		<input type="hidden" name="tasktype" value="bcvstap">
		
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
	<div class = 'wrapper'>
<g:render template="/index/menu" />
		<h2>BCV pipeline</h2>
		<noscript>
 For full functionality of this site it is necessary to enable JavaScript.
 Here are the <a href="http://www.enable-javascript.com/">
 instructions how to enable JavaScript in your web browser</a>.
</noscript>
		
<p> BCV online makes use of BCV and STAP (taxonomy identification tool) to perform complete chromatogram analysis - from base calling to taxonomic assignment of predicted sequences. The current version of BCV online tool is intended for processing .ab1 chromatogram files, obtained by direct sequencing of 16S rRNA gene from clinical samples. Please refer to the <a href="${createLinkTo(dir: '/index', file: 'userguide.gsp')}">user guide</a> for brief algorithm overview and output format explanation. </p>
		<g:form controller="job" action="submitbcv" enctype="multipart/form-data" onsubmit="return validateForm()">
		
			<div class='header'> Enter your sample </div>
			
			<div class='panel'>
			Upload chromatograms in .ab1 format
			<p>
			<div class="inputWrapper">Browse
				<label><input type='file' name='files' id='files' class='files' accept='.ab1' multiple onchange='displayList()' /></label>
			</div>
			<div class = 'error'><label id = 'file_error'></label></div>
			<p><table class='fileTable' id='fileTable'></table></div>
			
			<div class='header'> BCV options </div>
			
			<div class='panel'>
			<table class='options'>
			<tr><input type="hidden" name = "deletedFiles" id="deletedFiles"></tr>
		<!--			<tr> -->
		<!--				<td>Vocabulary <img src='<g:createLinkTo dir='images' file='tooltip_icon.gif'/>' title ='Vocabulary is a multiple alignment of sequences, which contains sequences similar to the expected DNA variants in the sample. For analysis of human clinical samples, use "human microbiome" vocabulary.' ></td> -->
		<!--				<td><select name='vocabulary'> -->
		<!--					<option selected='selected' value='human microbiome'>human microbiome</option> -->
		<!--					<option value='16s ridom'>16S ridom</option> -->
		<!--					</select> -->
		<!--				</td> -->
		<!--			</tr> -->
				
		<tr><input type="hidden" name="vocabulary" value="human microbiome"></tr>
					<tr>
						<td>Taxonomic database <img src='<g:createLinkTo dir='images' file='tooltip_icon.gif'/>' title ='GreenGenes database, used for taxonomic affiliation, contains sequences with automatically assigned taxonomy, which is reliable but not exhaustive. For some of these sequences, source organisms were also specified by the authors who uploaded them. If you choose option "named isolates", only such sequences will be used for taxonomy identification. Thus it is guaranteed that relatives with species-level taxonomic annotation will be present in the output, which is not the case with the full database. Since this manual annotation often helps to enhance taxonomic resolution, it is recommended that you choose this option. On the other hand, this annotation cannot be considered reliable; moreover, the full database may contain closer relatives with different taxonomic affiliation.' id='mytooltip' name='mytooltip'></td>
						<td><select name='taxdb'>
							<option selected='named isolates' value='named isolates'>named isolates</option>
							<option value='full'>full</option>
							</select>
						</td>
					</tr>
					<tr>
						<td>Maximum distance<br>between close taxons <img src='<g:createLinkTo dir='images' file='tooltip_icon.gif'/>' title ='If STAP cannot decide, to which one of the two or more taxonomic groups the query sequence should be assigned, it outputs either the lowest common taxonomic group, or all the possible groups, if the difference between them (maximum edit distance between their members) is less than the value you set here.' id='mytooltip'></td>
						<td><input type='text' name='distance' id='distance' value='0,03' size='10' maxlength='30' />
						</td>
					</tr>
					<tr>
						<td colspan = '2'><div class = 'error'><label id = 'distance_error'></label></div></td>
					</tr>	
			</table></div>
			<div class='header'>Submit your job </div>
			
			<div class='panel'>
			<label class ='collapsing-toggler'><input type='checkbox' name='checkbox_email' id='checkbox_email' value='ON' checked='checked' />Send results by E-mail</label>
			<p><div class='collapsing-panel'><input type='text' name='email' id='email' size='50' maxlength='80' /></div>
			<div class = 'error'><label id = 'email_error'></label></div><p>
			<div class="inputWrapper">Submit
				<p><input type='submit' name='submit' id = 'submit' value='Submit' />
			</div>
			</div>
			<div class = 'error'><label id = 'final_error'></label></div><p>
		</g:form> 
			
			<div class ="push"></div>
    </div>
    <g:render template="/index/footer" />
		<script>
		$(document).ready(function(){
		
			$(".collapsing-toggler").change(function()
			{
					if($("#checkbox_email").prop("checked")){
					$(this).nextAll(".collapsing-panel:first").slideDown(400);
					}
					else {
					$(this).nextAll(".collapsing-panel:first").slideUp(400);
					document.getElementById("email_error").innerHTML = "";
					}
			});
			});
			
		$('#submit').on('click', function() {
 	 	ga('send', 'event', 'button', 'click', 'submit');
		});
		
			function displayList(){
				document.getElementById("file_error").innerHTML = "";
				var deleted = document.getElementById("deletedFiles");
				deleted.value = "";
				var files = document.getElementById("files").files || [];
				var fileTable = document.getElementById("fileTable");
				
				for(var i = fileTable.rows.length;i>0;i--) {
					fileTable.deleteRow(i-1);
				}
				
				var header  = fileTable.insertRow(0);
				var headerLabel = document.createElement("td");
				headerLabel.setAttribute("colspan", 2);
				headerLabel.innerHTML = "Select primer direction:";
				header.appendChild(headerLabel);
				
				var row  = fileTable.insertRow(1);
				
				var th1 = document.createElement("th");
				th1.innerHTML = "File name";
				row.appendChild(th1);
				
				var th2 = document.createElement("th");
				th2.innerHTML = "Forward primer";
				row.appendChild(th2);
				
				
		
				for	(var index = 0; index < files.length; index++) {
					var row = fileTable.insertRow(index+2);
					var rowId = "row" + index;
					row.setAttribute("id", rowId);
					var filename = row.insertCell(0);
					filename.innerHTML = files[index].name;
					
					var isForward = row.insertCell(1);
					var newCheckBox = document.createElement("input");
					var checkBoxId = "checkbox" + index;
					
					var checked = check(files[index].name);
					
					newCheckBox.setAttribute("type","checkbox");
					newCheckBox.setAttribute("id",checkBoxId);
					newCheckBox.setAttribute("name",checkBoxId);
					
					if (checked){
						newCheckBox.setAttribute("checked", "true");
						newCheckBox.setAttribute("value","ON");
					}
					else {
						newCheckBox.setAttribute("value","OFF");
					}
					newCheckBox.setAttribute("onchange","changeDirection(this)");
					isForward.appendChild(newCheckBox);
		
					var newCheckBoxLabel = document.createElement("label");
					var checkBoxLabelId = "labelcheckbox" + index;
					newCheckBoxLabel.setAttribute("id",checkBoxLabelId);
					if (checked){
						newCheckBoxLabel.innerHTML = "forward";
					}
					else newCheckBoxLabel.innerHTML = "reverse";
					isForward.appendChild(newCheckBoxLabel);
					
					
					var deleterCell = row.insertCell(2);
					var deleter = document.createElement("img");
					var deleterId = "deleter" + index;
					deleter.setAttribute("id", deleterId);
					deleter.setAttribute("src", "${createLinkTo (dir:'images', file:'deleter.png')}");
					deleter.setAttribute("onclick", "deleteFile(this)");
					deleterCell.appendChild(deleter);
		
				}
		
				
			}
			function changeDirection(c){
				var labelId = "label" + c.id;
				if(c.checked){
					document.getElementById(labelId).innerHTML="forward";
					c.setAttribute("value","ON");
				}
				else {
					document.getElementById(labelId).innerHTML="reverse";
					c.setAttribute("value","OFF");
				}
			}
			
			
			function deleteFile(f){
			var index = parseInt(f.id.substring(7));
			var rowId = "row" + index;
			var row = document.getElementById(rowId);
    		row.parentNode.removeChild(row);
    		
    		document.getElementById("deletedFiles").value += "," + index;
			}
			
			function check(str){
			var t = str.split(".")[0].split("_");
			console.log(t);
			var probablePrimer = t[t.length-1];
			console.log(probablePrimer);
		
			var primerHash = {	Un161: true,
								un161: true,
								Un162: false,
								un162: false,
								Un16sq: false,
								un16sq: false,
								"534R": false,
								"534r": false,
								"27F": true,
								"27f": true,
								"341F": true,
								"341f": true
								};
			if (probablePrimer in primerHash){
				console.log(primerHash[probablePrimer]);
				return primerHash[probablePrimer];
			}
			else {
				console.log("nope");
				return false;
			}
			}
			
			function validateForm() { 
			var passed = true;
			var x = document.getElementById("email").value;
			var test_email = true;
			var atpos = x.indexOf("@");
			var dotpos = x.lastIndexOf(".");
			if (atpos< 1 || dotpos<atpos+2 || dotpos+2>=x.length) {
				test_email = false;
			}
			if (!test_email & document.getElementById("checkbox_email").checked){
				document.getElementById("email_error").innerHTML = "Invalid e-mail";
				passed = false;
			}
			else {
				document.getElementById("email_error").innerHTML = "";
			}
			
			var files = document.getElementById("files").files || [];
			
			var wereDeleted;
			if (document.getElementById("deletedFiles").value === ""){
				wereDeleted = 0;
				console.log("none deleted");
			}
			else {
				wereDeleted = document.getElementById("deletedFiles").value.split(",").length-1;
				console.log(wereDeleted + " deleted ");
			}
			
			
			var files_test = true;
			if (document.getElementById("files").value === "" || files.length === wereDeleted){
				document.getElementById("file_error").innerHTML = "Select at least one file";
				files_test = false;
			}
			else {
			
				if (files.length - wereDeleted > 10){
					document.getElementById("file_error").innerHTML = "Please, do not select more than 10 files.";
					files_test = false;
				}
				else {
				for	(var index = 0; index < files.length; index++) {
					var myfile = files[index].name;
					console.log(myfile);
					console.log(myfile.substr(myfile.lastIndexOf("."), 4));
					if (myfile.substr(myfile.lastIndexOf("."), 4) !== ".ab1"){
						document.getElementById("file_error").innerHTML = "Unsupported extension";
						files_test = false;
						break;
					}
				}
				}
			}
			
			
				
			if (files_test === true){
				document.getElementById("file_error").innerHTML = "";
			}
			else {
				passed = false;
			}
			
			var dist_check = document.getElementById("distance").value.toString().replace(',', '.');
			if (!isNaN(dist_check) && dist_check.toString().indexOf('.') != -1 && parseFloat(dist_check) >= 0 && parseFloat(dist_check) <= 0.1 && document.getElementById("distance").value.indexOf(',') != -1 ){
				document.getElementById("distance_error").innerHTML = "";
			}
			else {
				document.getElementById("distance_error").innerHTML = "Enter a comma-separated number. Valid range is 0.. 0,1";
				passed = false;
			}
		
			if (passed === false){
				document.getElementById("final_error").innerHTML = "There is something wrong with the data you provided."
			}
			return passed;
		} 
		
		
		

		
		
		
			
		</script>

	</body>
</html>























