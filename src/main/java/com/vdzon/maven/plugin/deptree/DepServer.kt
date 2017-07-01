package com.vdzon.maven.plugin.deptree


import com.vdzon.maven.plugin.deptree.model.ModuleGroups
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.util.resource.Resource
import org.glassfish.jersey.servlet.ServletContainer
//import org.slf4j.LoggerFactory

fun main(args: Array<String>) {
    val depServer = DepServer()
    depServer.start()
}

class Log {
    fun info(s:String):Unit {
        println(s)
    }
}

class DepServer {


    companion object {
        var log = Log()
    }

    fun start() {
        log.info("load groups.yaml")

        val moduleGroupService = ModuleGroupService()
        val yamlFilename = "groups.yaml"
        val moduleGroups: ModuleGroups = moduleGroupService.getExistsingOrNewModuleGroups(yamlFilename, moduleGroupService)

        moduleGroups.moduleGroups.forEach {
            it.layers.forEach {
                it.modules.forEach {
                    it.deps = listOf("test1:test1" , "test2:test2")
                }
            }
        }


        moduleGroups.moduleGroups.forEach {
            log.info(it.modulegroup)
            it.layers.forEach {
                log.info(" -" + it.name)
                it.modules.forEach {
                    log.info("  -" + it.name)
                    it.deps.forEach {
                        log.info("   -" + it)
                    }
                }
            }
        }


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
        val url = StartPlugin::class.java!!.getClassLoader().getResource("web/")
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
