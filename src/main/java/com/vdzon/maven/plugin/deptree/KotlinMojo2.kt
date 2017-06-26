package com.vdzon.maven.plugin.deptree


import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.glassfish.jersey.servlet.ServletContainer
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.util.resource.Resource


@Mojo(name = "start")
class KotlinMojo2 : AbstractMojo() {

    @Parameter
    private val foo: String? = null

    @Parameter
    private val bar: String? = null

    @Throws(MojoExecutionException::class)
    override fun execute() {
        log.info("start server!!")

        setLoggingLevel()

        val server = buildServer()
        val contextHandler = createContextHandler()
        server.handler = contextHandler
        startServer(server)


    }

    private fun createContextHandler(): ServletContextHandler {
        val contextHandler = ServletContextHandler(ServletContextHandler.SESSIONS)
        addStaticFiles(contextHandler)
        addJerseyServlet(contextHandler)
        addDefaultServlet(contextHandler)
        return contextHandler
    }

    private fun addDefaultServlet(context: ServletContextHandler) {
        val holderPwd = ServletHolder("default", DefaultServlet::class.java)
        holderPwd.setInitParameter("dirAllowed", "true")
        context.addServlet(holderPwd, "/")
    }

    private fun startServer(server: Server) {
        try {
            server.start()
            server.dump(System.err)
            server.join()
        } catch (t: Throwable) {
            t.printStackTrace(System.err)
        }
    }

    private fun addStaticFiles(context: ServletContextHandler) {
        val pwdPath = System.getProperty("user.dir")
        context.resourceBase = pwdPath
        context.contextPath = "/"
        val url = KotlinMojo2::class.java!!.getClassLoader().getResource("web/")
        val webRootUri = url!!.toURI()
        context.setBaseResource(Resource.newResource(webRootUri));
    }

    private fun buildServer(): Server {
        val server = Server()
        val connector = ServerConnector(server)
        connector.port = 8080
        server.addConnector(connector)
        return server
    }

    private fun setLoggingLevel() {
//        System.setProperty("org.eclipse.jetty.LEVEL", "ERR")
    }

    private fun addJerseyServlet(context: ServletContextHandler) {
        val jerseyServlet = context.addServlet(ServletContainer::class.java, "/rest/*")
        jerseyServlet.initOrder = 0
        jerseyServlet.setInitParameter("jersey.config.server.provider.packages", "com.vdzon.maven.plugin.deptree")
    }


}
