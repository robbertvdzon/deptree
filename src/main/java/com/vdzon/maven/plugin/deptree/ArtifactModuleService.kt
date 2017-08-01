package com.vdzon.maven.plugin.deptree

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.vdzon.maven.plugin.deptree.jsonmodel.ArtifactModules
import java.io.File


class ArtifactModuleService {
    fun serializeArtifactModules(artifactModules: ArtifactModules): String {
        val mapper = ObjectMapper(YAMLFactory())
        return mapper.writeValueAsString(artifactModules)
    }

    fun deserializeArtifactModules(yaml: String): ArtifactModules {
        val mapper = ObjectMapper(YAMLFactory())
        return mapper.readValue(yaml, ArtifactModules::class.java)
    }

    fun getExistsingOrNewArtifactModules(yamlFilename: String, artifactModuleService: ArtifactModuleService) = if (artifactModulesFileExists(yamlFilename)) readArtifactModules(artifactModuleService, yamlFilename) else ArtifactModules()

    private fun readArtifactModules(artifactModuleService: ArtifactModuleService, yamlFilename: String) = artifactModuleService.deserializeArtifactModules(File(yamlFilename).readText(charset = Charsets.UTF_8))

    private fun artifactModulesFileExists(yamlFilename: String) = File(yamlFilename).exists()


}