import org.codehaus.groovy.grails.plugins.metadata.GrailsPlugin
import org.codehaus.groovy.grails.web.pages.GroovyPage
import org.codehaus.groovy.grails.web.taglib.*
import org.codehaus.groovy.grails.web.taglib.exceptions.GrailsTagException
import org.springframework.web.util.*
import grails.util.GrailsUtil

class gsp_bcvapp_bcvjobform_gsp extends GroovyPage {
public String getGroovyPageFileName() { "/WEB-INF/grails-app/views/bcvjob/form.gsp" }
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
invokeTag('captureMeta','sitemesh',12,['gsp_sm_xmlClosingForEmptyTag':("/"),'http-equiv':("Content-Type"),'content':("text/html; charset=iso-8859-1")],-1)
printHtmlPart(6)
})
invokeTag('captureHead','sitemesh',26,[:],1)
printHtmlPart(7)
createTagBody(1, {->
printHtmlPart(8)
invokeTag('render','g',28,['template':("/index/menu")],-1)
printHtmlPart(9)
expressionOut.print(createLinkTo(dir: '/index', file: 'userguide.gsp'))
printHtmlPart(10)
createTagBody(2, {->
printHtmlPart(11)
invokeTag('createLinkTo','g',46,['dir':("images"),'file':("tooltip_icon.gif")],-1)
printHtmlPart(12)
invokeTag('createLinkTo','g',54,['dir':("images"),'file':("tooltip_icon.gif")],-1)
printHtmlPart(13)
invokeTag('createLinkTo','g',63,['dir':("images"),'file':("tooltip_icon.gif")],-1)
printHtmlPart(14)
})
invokeTag('form','g',79,['controller':("job"),'action':("submitbcv"),'enctype':("multipart/form-data"),'onsubmit':("return validateForm()")],2)
printHtmlPart(15)
})
invokeTag('captureBody','sitemesh',264,[:],1)
printHtmlPart(16)
}
public static final Map JSP_TAGS = new HashMap()
protected void init() {
	this.jspTags = JSP_TAGS
}
public static final String CONTENT_TYPE = 'text/html;charset=UTF-8'
public static final long LAST_MODIFIED = 1413799825738L
public static final String EXPRESSION_CODEC = 'html'
public static final String STATIC_CODEC = 'none'
public static final String OUT_CODEC = 'html'
public static final String TAGLIB_CODEC = 'none'
}
