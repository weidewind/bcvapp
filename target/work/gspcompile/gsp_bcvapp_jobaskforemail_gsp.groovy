import org.codehaus.groovy.grails.plugins.metadata.GrailsPlugin
import org.codehaus.groovy.grails.web.pages.GroovyPage
import org.codehaus.groovy.grails.web.taglib.*
import org.codehaus.groovy.grails.web.taglib.exceptions.GrailsTagException
import org.springframework.web.util.*
import grails.util.GrailsUtil

class gsp_bcvapp_jobaskforemail_gsp extends GroovyPage {
public String getGroovyPageFileName() { "/WEB-INF/grails-app/views/job/askforemail.gsp" }
public Object run() {
Writer out = getOut()
Writer expressionOut = getExpressionOut()
registerSitemeshPreprocessMode()
printHtmlPart(0)
createTagBody(1, {->
printHtmlPart(1)
createTagBody(2, {->
createClosureForHtmlPart(2, 3)
invokeTag('captureTitle','sitemesh',6,[:],3)
})
invokeTag('wrapTitleTag','sitemesh',6,[:],2)
printHtmlPart(3)
invokeTag('createLinkTo','g',7,['dir':("css"),'file':("snazzy.css")],-1)
printHtmlPart(4)
invokeTag('createLinkTo','g',8,['dir':("javascripts"),'file':("jquery-1.11.1.min.js")],-1)
printHtmlPart(5)
invokeTag('captureMeta','sitemesh',10,['gsp_sm_xmlClosingForEmptyTag':("/"),'http-equiv':("Content-Type"),'content':("text/html; charset=iso-8859-1")],-1)
printHtmlPart(6)
})
invokeTag('captureHead','sitemesh',12,[:],1)
printHtmlPart(7)
createTagBody(1, {->
printHtmlPart(8)
createTagBody(2, {->
printHtmlPart(9)
invokeTag('hiddenField','g',19,['name':("id"),'value':(params.id)],-1)
printHtmlPart(10)
invokeTag('hiddenField','g',20,['name':("task"),'id':("task"),'value':(params.task)],-1)
printHtmlPart(10)
invokeTag('submitButton','g',21,['class':("myButton"),'name':("wait"),'value':("I will wait here"),'onclick':("this.form.submited=this.name;")],-1)
printHtmlPart(10)
invokeTag('submitButton','g',22,['class':("myButton"),'name':("send"),'value':("OK, send them"),'onclick':("this.form.submited=this.name;")],-1)
printHtmlPart(11)
})
invokeTag('form','g',24,['controller':("job"),'action':("updateAndRun"),'onsubmit':("return validateForm(this.submited)")],2)
printHtmlPart(12)
})
invokeTag('captureBody','sitemesh',55,[:],1)
printHtmlPart(13)
}
public static final Map JSP_TAGS = new HashMap()
protected void init() {
	this.jspTags = JSP_TAGS
}
public static final String CONTENT_TYPE = 'text/html;charset=UTF-8'
public static final long LAST_MODIFIED = 1413366745939L
public static final String EXPRESSION_CODEC = 'html'
public static final String STATIC_CODEC = 'none'
public static final String OUT_CODEC = 'html'
public static final String TAGLIB_CODEC = 'none'
}
