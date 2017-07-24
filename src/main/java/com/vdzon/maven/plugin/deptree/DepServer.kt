package com.vdzon.maven.plugin.deptree


import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.vdzon.maven.plugin.deptree.enrichedmodel.EnrichedModule
import com.vdzon.maven.plugin.deptree.enrichedmodel.EnrichedModuleGroup
import com.vdzon.maven.plugin.deptree.enrichedmodel.EnrichedModuleGroups
import com.vdzon.maven.plugin.deptree.enrichedmodel.EnrichedModuleLayer
import com.vdzon.maven.plugin.deptree.jsonmodel.ModuleDependency
import com.vdzon.maven.plugin.deptree.jsonmodel.ModuleGroups
import com.vdzon.maven.plugin.deptree.resource.Builder
import java.io.File
import java.io.FileFilter
import java.util.*


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
        var data: EnrichedModuleGroups? = null;
    }

    fun start() {
        log.info("load groups.yaml")

        val moduleGroupService = ModuleGroupService()
        val yamlFilename = "groups.yaml"
        val moduleGroups: ModuleGroups = moduleGroupService.getExistsingOrNewModuleGroups(yamlFilename, moduleGroupService)

        DepServer.data = enrichModel(moduleGroups)

        generateWebPages()
    }

    private fun generateWebPages() {
        val htmlFile = File("target", "dependencies.html");
        val jsFile = File("target", "jsondata.js");
        val templateHtml = getTemplateHtmlFile()
        val jsonData = getJsonData()
        htmlFile.writeText(templateHtml)
        jsFile.writeText(jsonData)
    }

    private fun getJsonData(): String {
        val json = ObjectMapper().writeValueAsString(Builder.buildModel())
        return "var nodes = $json"
    }

    private fun getTemplateHtmlFile(): String {
        val classLoader = javaClass.classLoader
        val t = classLoader.getResourceAsStream("index.html")
        val s = Scanner(t).useDelimiter("\\A")
        val result = if (s.hasNext()) s.next() else ""
        return result;
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


}
