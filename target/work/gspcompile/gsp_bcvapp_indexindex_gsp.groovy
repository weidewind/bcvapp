import org.codehaus.groovy.grails.plugins.metadata.GrailsPlugin
import org.codehaus.groovy.grails.web.pages.GroovyPage
import org.codehaus.groovy.grails.web.taglib.*
import org.codehaus.groovy.grails.web.taglib.exceptions.GrailsTagException
import org.springframework.web.util.*
import grails.util.GrailsUtil

class gsp_bcvapp_indexindex_gsp extends GroovyPage {
public String getGroovyPageFileName() { "/WEB-INF/grails-app/views/index/index.gsp" }
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
invokeTag('createLinkTo','g',8,['dir':("css"),'file':("snazzy.css")],-1)
printHtmlPart(4)
invokeTag('createLinkTo','g',9,['dir':("javascripts"),'file':("jquery-1.11.1.min.js")],-1)
printHtmlPart(5)
invokeTag('captureMeta','sitemesh',11,['gsp_sm_xmlClosingForEmptyTag':("/"),'http-equiv':("Content-Type"),'content':("text/html; charset=iso-8859-1")],-1)
printHtmlPart(6)
})
invokeTag('captureHead','sitemesh',13,[:],1)
printHtmlPart(7)
createTagBody(1, {->
printHtmlPart(8)
invokeTag('menu','tmpl',17,[:],-1)
printHtmlPart(9)
expressionOut.print(createLinkTo(dir: '/index', file: 'installguide.gsp'))
printHtmlPart(10)
expressionOut.print(createLinkTo(dir: '/bcvjob', file: 'form.gsp'))
printHtmlPart(11)
createTagBody(2, {->
printHtmlPart(12)
invokeTag('actionSubmit','g',41,['class':("myButton"),'value':("BCV online"),'action':("bcvRedirect")],-1)
printHtmlPart(12)
invokeTag('actionSubmit','g',42,['class':("myButton"),'value':("Download & install"),'action':("installRedirect")],-1)
printHtmlPart(12)
invokeTag('actionSubmit','g',43,['class':("myButton"),'value':("User guide"),'action':("guideRedirect")],-1)
printHtmlPart(13)
})
invokeTag('form','g',45,['controller':("index")],2)
printHtmlPart(14)
})
invokeTag('captureBody','sitemesh',48,[:],1)
printHtmlPart(15)
}
public static final Map JSP_TAGS = new HashMap()
protected void init() {
	this.jspTags = JSP_TAGS
}
public static final String CONTENT_TYPE = 'text/html;charset=UTF-8'
public static final long LAST_MODIFIED = 1413799124629L
public static final String EXPRESSION_CODEC = 'html'
public static final String STATIC_CODEC = 'none'
public static final String OUT_CODEC = 'html'
public static final String TAGLIB_CODEC = 'none'
}
