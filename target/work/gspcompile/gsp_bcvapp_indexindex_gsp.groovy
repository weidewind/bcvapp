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
invokeTag('createLinkTo','g',8,['dir':("stylesheets"),'file':("regular.css")],-1)
printHtmlPart(4)
invokeTag('createLinkTo','g',9,['dir':("javascripts"),'file':("jquery-1.11.1.min.js")],-1)
printHtmlPart(5)
invokeTag('createLinkTo','g',10,['dir':("images"),', file':("myfavicon.ico")],-1)
printHtmlPart(6)
invokeTag('captureMeta','sitemesh',11,['gsp_sm_xmlClosingForEmptyTag':("/"),'http-equiv':("Content-Type"),'content':("text/html; charset=iso-8859-1")],-1)
printHtmlPart(1)
invokeTag('captureMeta','sitemesh',12,['gsp_sm_xmlClosingForEmptyTag':("/"),'name':("keywords"),'content':("DNA, sequencing, basecaller, chromatogram, mixture, deconvolution, Sanger, HMM, BCV")],-1)
printHtmlPart(7)
})
invokeTag('captureHead','sitemesh',26,[:],1)
printHtmlPart(8)
createTagBody(1, {->
printHtmlPart(9)
invokeTag('menu','tmpl',30,[:],-1)
printHtmlPart(10)
expressionOut.print(createLinkTo(dir: '/index', file: 'installguide.gsp'))
printHtmlPart(11)
expressionOut.print(createLinkTo(dir: '/bcvjob', file: 'form.gsp'))
printHtmlPart(12)
createTagBody(2, {->
printHtmlPart(13)
invokeTag('actionSubmit','g',56,['class':("myButton"),'value':("BCV online"),'action':("bcvRedirect")],-1)
printHtmlPart(13)
invokeTag('actionSubmit','g',57,['class':("myButton"),'value':("Download & install"),'action':("installRedirect")],-1)
printHtmlPart(13)
invokeTag('actionSubmit','g',58,['class':("myButton"),'value':("User guide"),'action':("guideRedirect")],-1)
printHtmlPart(14)
})
invokeTag('form','g',60,['controller':("index")],2)
printHtmlPart(15)
invokeTag('footer','tmpl',65,[:],-1)
printHtmlPart(8)
})
invokeTag('captureBody','sitemesh',66,[:],1)
printHtmlPart(16)
}
public static final Map JSP_TAGS = new HashMap()
protected void init() {
	this.jspTags = JSP_TAGS
}
public static final String CONTENT_TYPE = 'text/html;charset=UTF-8'
public static final long LAST_MODIFIED = 1435931360039L
public static final String EXPRESSION_CODEC = 'html'
public static final String STATIC_CODEC = 'none'
public static final String OUT_CODEC = 'html'
public static final String TAGLIB_CODEC = 'none'
}
