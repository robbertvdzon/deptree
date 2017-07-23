package com.vdzon.maven.plugin.deptree


import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.vdzon.maven.plugin.deptree.enrichedmodel.EnrichedModule
import com.vdzon.maven.plugin.deptree.enrichedmodel.EnrichedModuleGroup
import com.vdzon.maven.plugin.deptree.enrichedmodel.EnrichedModuleGroups
import com.vdzon.maven.plugin.deptree.enrichedmodel.EnrichedModuleLayer
import com.vdzon.maven.plugin.deptree.model.ModuleDependency
import com.vdzon.maven.plugin.deptree.model.ModuleGroups
import com.vdzon.maven.plugin.deptree.resource.Builder
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.servlet.DefaultServlet
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.eclipse.jetty.util.resource.Resource
import org.glassfish.jersey.servlet.ServletContainer
import java.io.File
import java.io.FileFilter

//import org.slf4j.LoggerFactory

fun main(args: Array<String>) {
    val depServer = DepServer()
    depServer.start()
}

class Log {
    fun info(s: String): Unit {
        println(s)
    }
}

class DepServer {


    companion object {
        var log = Log()
        var data:EnrichedModuleGroups? = null;



    }

    fun start() {
        log.info("load groups.yaml")

        val moduleGroupService = ModuleGroupService()
        val yamlFilename = "groups.yaml"
        val moduleGroups: ModuleGroups = moduleGroupService.getExistsingOrNewModuleGroups(yamlFilename, moduleGroupService)

        DepServer.data = enrichModel(moduleGroups)

        buildDataJson()
        copyWebResources()

        log.info("start server!!")

        setLoggingLevel()

        val server = buildServer()
        val contextHandler = createContextHandler()
        server.handler = contextHandler
        startServer(server)


    }

    private fun copyWebResources() {
        val classLoader = javaClass.classLoader
        val file: File = File(classLoader.getResource("index.html")!!.file)
        file.copyTo(File("target","index.html"), overwrite = true)
    }

    private fun buildDataJson() {
        val json = ObjectMapper().writeValueAsString(Builder.buildModel())
        File("target", "nodes.json").writeText(json, charset = Charsets.UTF_8)
    }

    private fun enrichModel(moduleGroups: ModuleGroups): EnrichedModuleGroups {

        // copy moduleGroup
        val enrichedModuleGroups = EnrichedModuleGroups(
                moduleGroups.application,
                moduleGroups.moduleGroups
                        .map {
                            EnrichedModuleGroup(
                                    it.modulegroup,
                                    null,
                                    it.layers.map {
                                        EnrichedModuleLayer(
                                                it.name,
                                                null,
                                                it.modules.map {
                                                    EnrichedModule(
                                                            it.name
                                                    )
                                                }
                                        )
                                    })
                        })
        // add parents
        enrichedModuleGroups.moduleGroups.forEach {
            val moduleGroup = it
            moduleGroup.modulegroups = enrichedModuleGroups
            moduleGroup.layers.forEach {
                val layer = it
                layer.moduleGroup = moduleGroup
                layer.modules.forEach {
                    val module = it
                    module.moduleLayer = layer
                }
            }
        }

        // place all modules in a map
        val allModules = enrichedModuleGroups
                .moduleGroups
                .flatMap {
                    it.layers.flatMap {
                        it.modules.map { it }
                    }
                }
                .associateBy({ it.name }, { it })

        // fill in all depTo
        File("target").listFiles(FileFilter { it.name.endsWith("yaml") }).forEach {
            val mapper = ObjectMapper(YAMLFactory())

            val moduleDependency = mapper.readValue(it.readText(charset = Charsets.UTF_8), ModuleDependency::class.java)
            val module = allModules.get("${moduleDependency.groupId}:${moduleDependency.artifactId}")
            if (module != null) {
                moduleDependency.deps.forEach {
                    val depModule = allModules.get(it)
                    if (depModule != null) {
                        module.depsTo = module.depsTo.plus(depModule)
                    }
                }
            }
        }

        // fill in all depFrom
        allModules.values.forEach {
            val module = it
            module.depsFrom = allModules.values.filter { it.depsTo.contains(module) }
        }

        return enrichedModuleGroups
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
