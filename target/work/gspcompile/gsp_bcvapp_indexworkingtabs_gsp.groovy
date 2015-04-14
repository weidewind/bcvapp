import org.codehaus.groovy.grails.plugins.metadata.GrailsPlugin
import org.codehaus.groovy.grails.web.pages.GroovyPage
import org.codehaus.groovy.grails.web.taglib.*
import org.codehaus.groovy.grails.web.taglib.exceptions.GrailsTagException
import org.springframework.web.util.*
import grails.util.GrailsUtil

class gsp_bcvapp_indexworkingtabs_gsp extends GroovyPage {
public String getGroovyPageFileName() { "/WEB-INF/grails-app/views/index/workingtabs.gsp" }
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
invokeTag('createLinkTo','g',10,['dir':("javascripts"),'file':("jquery-ui.js")],-1)
printHtmlPart(6)
invokeTag('createLinkTo','g',11,['dir':("css"),'file':("jquery-ui.css")],-1)
printHtmlPart(7)
invokeTag('captureMeta','sitemesh',14,['gsp_sm_xmlClosingForEmptyTag':("/"),'http-equiv':("Content-Type"),'content':("text/html; charset=iso-8859-1")],-1)
printHtmlPart(8)
})
invokeTag('captureHead','sitemesh',28,[:],1)
printHtmlPart(9)
createTagBody(1, {->
printHtmlPart(10)
expressionOut.print(createLink(controller: 'index', action: 'index'))
printHtmlPart(11)
expressionOut.print(createLink(controller: 'index', action: 'installguide'))
printHtmlPart(12)
expressionOut.print(createLink(controller: 'index', action: 'userguide'))
printHtmlPart(13)
expressionOut.print(createLink(controller: 'bcvjob', action: 'form'))
printHtmlPart(14)
expressionOut.print(createLink(controller: 'stapjob', action: 'stapform'))
printHtmlPart(15)
createClosureForHtmlPart(16, 2)
invokeTag('remoteLink','g',44,['controller':("bcvjob"),', action':("form")],2)
printHtmlPart(17)
expressionOut.print(createLink(controller: 'index', action: 'index'))
printHtmlPart(18)
expressionOut.print(createLink(controller: 'bcvjob', action: 'form'))
printHtmlPart(19)
})
invokeTag('captureBody','sitemesh',86,[:],1)
printHtmlPart(20)
}
public static final Map JSP_TAGS = new HashMap()
protected void init() {
	this.jspTags = JSP_TAGS
}
public static final String CONTENT_TYPE = 'text/html;charset=UTF-8'
public static final long LAST_MODIFIED = 1413806399010L
public static final String EXPRESSION_CODEC = 'html'
public static final String STATIC_CODEC = 'none'
public static final String OUT_CODEC = 'html'
public static final String TAGLIB_CODEC = 'none'
}
