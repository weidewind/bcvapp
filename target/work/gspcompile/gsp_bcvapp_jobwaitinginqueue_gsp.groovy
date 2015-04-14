import org.codehaus.groovy.grails.plugins.metadata.GrailsPlugin
import org.codehaus.groovy.grails.web.pages.GroovyPage
import org.codehaus.groovy.grails.web.taglib.*
import org.codehaus.groovy.grails.web.taglib.exceptions.GrailsTagException
import org.springframework.web.util.*
import grails.util.GrailsUtil

class gsp_bcvapp_jobwaitinginqueue_gsp extends GroovyPage {
public String getGroovyPageFileName() { "/WEB-INF/grails-app/views/job/waitinginqueue.gsp" }
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
invokeTag('createLinkTo','g',8,['dir':("images"),', file':("myfavicon.ico")],-1)
printHtmlPart(5)
invokeTag('createLinkTo','g',9,['dir':("javascripts"),'file':("jquery-1.11.1.min.js")],-1)
printHtmlPart(6)
invokeTag('captureMeta','sitemesh',11,['gsp_sm_xmlClosingForEmptyTag':("/"),'http-equiv':("Content-Type"),'content':("text/html; charset=iso-8859-1")],-1)
printHtmlPart(7)
})
invokeTag('captureHead','sitemesh',24,[:],1)
printHtmlPart(8)
createTagBody(1, {->
printHtmlPart(9)
expressionOut.print(start)
printHtmlPart(10)
createTagBody(2, {->
printHtmlPart(11)
expressionOut.print(sessionId)
printHtmlPart(12)
expressionOut.print(task)
printHtmlPart(12)
expressionOut.print(id)
printHtmlPart(12)
expressionOut.print(dateCreated)
printHtmlPart(13)
expressionOut.print(remoteFunction(controller: 'job', action: 'updateTimeStamp', update: 'randomString', params: '{timeStamp:timeStamp, sessionId:sessionId, waitingType:"queue"}'))
printHtmlPart(14)
expressionOut.print(remoteFunction(controller: 'job', action: 'queueIsFinished', update: 'queueIsFinished', params: '{dateCreated:dateCreated}'))
printHtmlPart(15)
expressionOut.print(remoteFunction(controller: 'job', action: 'runner', update: 'waiting', onSuccess:'loadWaiting(data, task, sessionId);', params: '{task:task, id:id}' ))
printHtmlPart(16)
expressionOut.print(createLink (controller:'job', action:'waitingPage', params:'{task:task, sessionId:sessionId}'))
printHtmlPart(17)
expressionOut.print(createLink (controller:'job', action:'waitingPage', params:[task:task, sessionId:sessionId]))
printHtmlPart(18)
})
invokeTag('javascript','g',69,[:],2)
printHtmlPart(19)
})
invokeTag('captureBody','sitemesh',70,[:],1)
printHtmlPart(20)
}
public static final Map JSP_TAGS = new HashMap()
protected void init() {
	this.jspTags = JSP_TAGS
}
public static final String CONTENT_TYPE = 'text/html;charset=UTF-8'
public static final long LAST_MODIFIED = 1417175690537L
public static final String EXPRESSION_CODEC = 'html'
public static final String STATIC_CODEC = 'none'
public static final String OUT_CODEC = 'html'
public static final String TAGLIB_CODEC = 'none'
}
