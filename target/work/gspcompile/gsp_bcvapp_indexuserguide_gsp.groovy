import org.codehaus.groovy.grails.plugins.metadata.GrailsPlugin
import org.codehaus.groovy.grails.web.pages.GroovyPage
import org.codehaus.groovy.grails.web.taglib.*
import org.codehaus.groovy.grails.web.taglib.exceptions.GrailsTagException
import org.springframework.web.util.*
import grails.util.GrailsUtil

class gsp_bcvapp_indexuserguide_gsp extends GroovyPage {
public String getGroovyPageFileName() { "/WEB-INF/grails-app/views/index/userguide.gsp" }
public Object run() {
Writer out = getOut()
Writer expressionOut = getExpressionOut()
registerSitemeshPreprocessMode()
printHtmlPart(0)
createTagBody(1, {->
printHtmlPart(1)
invokeTag('captureMeta','sitemesh',4,['gsp_sm_xmlClosingForEmptyTag':("/"),'http-equiv':("Content-Type"),'content':("text/html; charset=iso-8859-1")],-1)
printHtmlPart(1)
invokeTag('captureMeta','sitemesh',5,['gsp_sm_xmlClosingForEmptyTag':("/"),'name':("description"),'content':("BCV project homepage")],-1)
printHtmlPart(1)
invokeTag('captureMeta','sitemesh',6,['gsp_sm_xmlClosingForEmptyTag':("/"),'name':("keywords"),'content':("DNA, sequencing, basecaller, chromatogram, mixture, deconvolution, Sanger, HMM, BCV")],-1)
printHtmlPart(1)
invokeTag('captureMeta','sitemesh',7,['gsp_sm_xmlClosingForEmptyTag':("/"),'name':("owner"),'content':("favorov@sensi.org")],-1)
printHtmlPart(2)
invokeTag('createLinkTo','g',8,['dir':("stylesheets"),'file':("regular.css")],-1)
printHtmlPart(3)
invokeTag('createLinkTo','g',9,['dir':("images"),', file':("myfavicon.ico")],-1)
printHtmlPart(4)
createTagBody(2, {->
createClosureForHtmlPart(5, 3)
invokeTag('captureTitle','sitemesh',10,[:],3)
})
invokeTag('wrapTitleTag','sitemesh',10,[:],2)
printHtmlPart(6)
})
invokeTag('captureHead','sitemesh',23,[:],1)
printHtmlPart(7)
createTagBody(1, {->
printHtmlPart(8)
invokeTag('menu','tmpl',26,[:],-1)
printHtmlPart(9)
expressionOut.print(createLink(controller: 'job', action: 'downloadFile' , params: [path: '/store/home/bcvapp/bcvapp/web-app/pipeline/bcv-userguide.odt', contentType: 'application/vnd.oasis.opendocument.text', filename: 'bcv-userguide.odt']))
printHtmlPart(10)
invokeTag('footer','tmpl',51,[:],-1)
printHtmlPart(7)
})
invokeTag('captureBody','sitemesh',52,[:],1)
printHtmlPart(11)
}
public static final Map JSP_TAGS = new HashMap()
protected void init() {
	this.jspTags = JSP_TAGS
}
public static final String CONTENT_TYPE = 'text/html;charset=UTF-8'
public static final long LAST_MODIFIED = 1421065282159L
public static final String EXPRESSION_CODEC = 'html'
public static final String STATIC_CODEC = 'none'
public static final String OUT_CODEC = 'html'
public static final String TAGLIB_CODEC = 'none'
}
