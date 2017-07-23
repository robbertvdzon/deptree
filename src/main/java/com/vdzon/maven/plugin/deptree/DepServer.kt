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

        buildHtmlFile()
    }

    private fun buildHtmlFile() {
        val newHtmlFile = File("target", "dependencies.html");
        val templateHtml = getTemplateHtmlFile()
        val jsonData = getJsonData()
        newHtmlFile.writeText(replaceSampleDataWithJson(templateHtml, jsonData))
    }

    private fun replaceSampleDataWithJson(text: String, json: String?): String {
        val startIndex = text.indexOf("START SAMPLE DATA")
        val endIndex = text.indexOf("END SAMPLE DATA")
        val part1 = text.substring(0, startIndex)
        val part2 = text.substring(endIndex + "END SAMPLE DATA".length)
        var newHtml = "$part1 GENERATED DATA\n    var nodes = $json \n$part2"
        return newHtml
    }

    private fun getJsonData(): String? {
        val json = ObjectMapper().writeValueAsString(Builder.buildModel())
        return json
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
