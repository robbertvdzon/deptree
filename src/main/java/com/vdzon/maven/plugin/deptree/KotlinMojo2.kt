package com.vdzon.maven.plugin.deptree


import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.eclipse.jetty.server.Handler
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.server.handler.HandlerList
import org.eclipse.jetty.server.handler.ResourceHandler
import org.glassfish.jersey.servlet.ServletContainer


@Mojo(name = "start")
class KotlinMojo2 : AbstractMojo() {

    @Parameter
    private val foo: String? = null

    @Parameter
    private val bar: String? = null

    @Throws(MojoExecutionException::class)
    override fun execute() {
        log.info("start server")

        val jettyServer = Server(8080)

        val context = ServletContextHandler(ServletContextHandler.SESSIONS)
        context.contextPath = "/"
        addJerseyServlet(context)

        val handlers = HandlerList()
        handlers.handlers = arrayOf<Handler>(createStaticResourceHandler(), context)

        jettyServer.setHandler(handlers)

        runJetty(jettyServer)

    }

    private fun runJetty(jettyServer: Server) {
        try {
            jettyServer.start()
            jettyServer.join()
        } finally {
            jettyServer.destroy()
        }
    }

    private fun createStaticResourceHandler(): ResourceHandler {
        val resource_handler = ResourceHandler()
        resource_handler.resourceBase = "src/main/resources/web/"
        return resource_handler
    }

    private fun addJerseyServlet(context: ServletContextHandler) {
        val jerseyServlet = context.addServlet(
                ServletContainer::class.java, "/*")
        jerseyServlet.initOrder = 0
        jerseyServlet.setInitParameter("jersey.config.server.provider.packages", "com.vdzon.maven.plugin.deptree")
    }


}
