package com.vdzon.maven.plugin.deptree


import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.vdzon.maven.plugin.deptree.enrichedmodel.EnrichedArtifact
import com.vdzon.maven.plugin.deptree.enrichedmodel.EnrichedArtifactGroup
import com.vdzon.maven.plugin.deptree.enrichedmodel.EnrichedArtifactGroups
import com.vdzon.maven.plugin.deptree.enrichedmodel.EnrichedArtifactLayer
import com.vdzon.maven.plugin.deptree.jsonmodel.ArtifactDependency
import com.vdzon.maven.plugin.deptree.jsonmodel.ArtifactGroups
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
        var data: EnrichedArtifactGroups? = null;
    }

    fun start() {
        log.info("load groups.yaml")

        val artifactGroupService = ArtifactGroupService()
        val yamlFilename = "groups.yaml"
        val artifactGroups: ArtifactGroups = artifactGroupService.getExistsingOrNewArtifactGroups(yamlFilename, artifactGroupService)

        DepServer.data = enrichModel(artifactGroups)

        generateWebPages()
    }

    private fun generateWebPages() {
        val jsFile = File("target", "jsondata.js");
        val jsonData = getJsonData()
        jsFile.writeText(jsonData)
        copyWebfile("dependencies.html","index.html")
        copyWebfile("groups.js","groups.js")
        copyWebfile("artifacts.js","artifacts.js")
        copyWebfile("depgroupgroup.js","depgroupgroup.js")
        copyWebfile("depbase.js","depbase.js")
        copyWebfile("depgroupartifact.js","depgroupartifact.js")
        copyWebfile("departifactgroup.js","departifactgroup.js")
        copyWebfile("departifactartifact.js","departifactartifact.js")
    }

    private fun copyWebfile(filename:String, sourceFilename:String) {
        val fileData = File("target", filename);
        val templateData = getTemplateFile(sourceFilename)
        fileData .writeText(templateData)
    }

    private fun getJsonData(): String {
        val json = ObjectMapper().writeValueAsString(Builder.buildModel())
        return "var nodes = $json"
    }

    private fun getTemplateFile(filename:String): String {
        val classLoader = javaClass.classLoader
        val t = classLoader.getResourceAsStream(filename)
        val s = Scanner(t).useDelimiter("\\A")
        val result = if (s.hasNext()) s.next() else ""
        return result;
    }

    private fun enrichModel(artifactGroups: ArtifactGroups): EnrichedArtifactGroups {
        // copy artifactGroup
        val enrichedArtifactsGroups = EnrichedArtifactGroups(
                artifactGroups.application,
                artifactGroups.artifactGroups
                        .map {
                            EnrichedArtifactGroup(
                                    it.artifactgroup,
                                    null,
                                    it.layers.map {
                                        EnrichedArtifactLayer(
                                                it.name,
                                                null,
                                                it.artifacts.map {
                                                    EnrichedArtifact(
                                                            it.name
                                                    )
                                                }
                                        )
                                    })
                        })
        // add parents
        enrichedArtifactsGroups.artifactGroups.forEach {
            val artifactGroup = it
            artifactGroup.artifactgroups = enrichedArtifactsGroups
            artifactGroup.layers.forEach {
                val layer = it
                layer.artifactGroup = artifactGroup
                layer.artifacts.forEach {
                    val artifact = it
                    artifact.artifactLayer = layer
                }
            }
        }

        // place all artifacts in a map
        val allArtifacts = enrichedArtifactsGroups
                .artifactGroups
                .flatMap {
                    it.layers.flatMap {
                        it.artifacts.map { it }
                    }
                }
                .associateBy({ it.name }, { it })

        // fill in all depTo
        File("target").listFiles(FileFilter { it.name.endsWith("yaml") }).forEach {
            val mapper = ObjectMapper(YAMLFactory())

            val artifactDependency = mapper.readValue(it.readText(charset = Charsets.UTF_8), ArtifactDependency::class.java)
            val artifact = allArtifacts.get("${artifactDependency.groupId}:${artifactDependency.artifactId}")
            if (artifact != null) {
                artifactDependency.deps.forEach {
                    val depArtifact = allArtifacts.get(it)
                    if (depArtifact != null) {
                        artifact.depsTo = artifact.depsTo.plus(depArtifact)
                    }
                }
            }
        }

        // fill in all depFrom
        allArtifacts.values.forEach {
            val artifact = it
            artifact.depsFrom = allArtifacts.values.filter { it.depsTo.contains(artifact) }
        }

        return enrichedArtifactsGroups
    }


}
