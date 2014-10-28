eventConfigureTomcat = {tomcat ->
	def ctx=tomcat.host.findChild(serverContextPath)
	if ( ctx.aliases ) {
		ctx.aliases += ',/results=/store/home/bcvapp/bcvapp/web-app/results'
	} else {
		ctx.aliases = '/results=/store/home/bcvapp/bcvapp/web-app/results'
	}
}