import org.codehaus.groovy.grails.plugins.metadata.GrailsPlugin
import org.codehaus.groovy.grails.web.pages.GroovyPage
import org.codehaus.groovy.grails.web.taglib.*
import org.codehaus.groovy.grails.web.taglib.exceptions.GrailsTagException
import org.springframework.web.util.*
import grails.util.GrailsUtil

class gsp_bcvapp_index_menu_gsp extends GroovyPage {
public String getGroovyPageFileName() { "/WEB-INF/grails-app/views/index/_menu.gsp" }
public Object run() {
Writer out = getOut()
Writer expressionOut = getExpressionOut()
registerSitemeshPreprocessMode()
printHtmlPart(0)
expressionOut.print(createLinkTo(dir: '/index', file: 'index.gsp'))
printHtmlPart(1)
expressionOut.print(createLinkTo(dir: '/index', file: 'installguide.gsp'))
printHtmlPart(2)
expressionOut.print(createLinkTo(dir: '/index', file: 'userguide.gsp'))
printHtmlPart(3)
expressionOut.print(createLinkTo(dir: '/bcvjob', file: 'form.gsp'))
printHtmlPart(4)
expressionOut.print(createLinkTo(dir: '/stapjob', file: 'stapform.gsp'))
printHtmlPart(5)
}
public static final Map JSP_TAGS = new HashMap()
protected void init() {
	this.jspTags = JSP_TAGS
}
public static final String CONTENT_TYPE = 'text/html;charset=UTF-8'
public static final long LAST_MODIFIED = 1413710916890L
public static final String EXPRESSION_CODEC = 'html'
public static final String STATIC_CODEC = 'none'
public static final String OUT_CODEC = 'html'
public static final String TAGLIB_CODEC = 'none'
}
