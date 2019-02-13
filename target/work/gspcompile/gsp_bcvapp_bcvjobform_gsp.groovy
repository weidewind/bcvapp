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
invokeTag('createLinkTo','g',8,['dir':("stylesheets"),'file':("regular.css")],-1)
printHtmlPart(4)
invokeTag('createLinkTo','g',9,['dir':("images"),', file':("myfavicon.ico")],-1)
printHtmlPart(5)
invokeTag('createLinkTo','g',10,['dir':("javascripts"),'file':("jquery-1.11.1.min.js")],-1)
printHtmlPart(6)
invokeTag('captureMeta','sitemesh',13,['gsp_sm_xmlClosingForEmptyTag':("/"),'http-equiv':("Content-Type"),'content':("text/html; charset=iso-8859-1")],-1)
printHtmlPart(7)
})
invokeTag('captureHead','sitemesh',25,[:],1)
printHtmlPart(8)
createTagBody(1, {->
printHtmlPart(9)
invokeTag('render','g',28,['template':("/index/menu")],-1)
printHtmlPart(10)
expressionOut.print(createLinkTo(dir: '/index', file: 'userguide.gsp'))
printHtmlPart(11)
createTagBody(2, {->
printHtmlPart(12)
invokeTag('createLinkTo','g',62,['dir':("images"),'file':("tooltip_icon.gif")],-1)
printHtmlPart(13)
invokeTag('createLinkTo','g',72,['dir':("images"),'file':("tooltip_icon.gif")],-1)
printHtmlPart(14)
invokeTag('createLinkTo','g',80,['dir':("images"),'file':("tooltip_icon.gif")],-1)
printHtmlPart(15)
})
invokeTag('form','g',98,['controller':("job"),'action':("submitbcv"),'enctype':("multipart/form-data"),'onsubmit':("return validateForm()")],2)
printHtmlPart(16)
invokeTag('render','g',104,['template':("/index/footer")],-1)
printHtmlPart(17)
expressionOut.print(createLink(controller: 'job', action: 'downloadChrom'))
printHtmlPart(18)
expressionOut.print(createLinkTo (dir:'images', file:'mydownload.png'))
printHtmlPart(19)
expressionOut.print(createLinkTo (dir:'images', file:'mydeleter.png'))
printHtmlPart(20)
expressionOut.print(remoteFunction(controller: 'job', action: 'downloadChrom', params: '{filename:filename}'))
printHtmlPart(21)
})
invokeTag('captureBody','sitemesh',492,[:],1)
printHtmlPart(22)
}
public static final Map JSP_TAGS = new HashMap()
protected void init() {
	this.jspTags = JSP_TAGS
}
public static final String CONTENT_TYPE = 'text/html;charset=UTF-8'
public static final long LAST_MODIFIED = 1550053284898L
public static final String EXPRESSION_CODEC = 'html'
public static final String STATIC_CODEC = 'none'
public static final String OUT_CODEC = 'html'
public static final String TAGLIB_CODEC = 'none'
}
