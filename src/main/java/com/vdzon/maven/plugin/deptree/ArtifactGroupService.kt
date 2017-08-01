package com.vdzon.maven.plugin.deptree

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.vdzon.maven.plugin.deptree.jsonmodel.ArtifactGroups
import java.io.File


class ArtifactGroupService {
    fun serializeArtifactGroups(artifactGroups: ArtifactGroups): String {
        val mapper = ObjectMapper(YAMLFactory())
        return mapper.writeValueAsString(artifactGroups)
    }

    fun deserializeArtifactGroups(yaml: String): ArtifactGroups {
        val mapper = ObjectMapper(YAMLFactory())
        return mapper.readValue(yaml, ArtifactGroups::class.java)
    }

    fun getExistsingOrNewArtifactGroups(yamlFilename: String, artifactGroupService: ArtifactGroupService) = if (artifactGroupsFileExists(yamlFilename)) readArtifactGroups(artifactGroupService, yamlFilename) else ArtifactGroups()

    private fun readArtifactGroups(artifactGroupService: ArtifactGroupService, yamlFilename: String) = artifactGroupService.deserializeArtifactGroups(File(yamlFilename).readText(charset = Charsets.UTF_8))

    private fun artifactGroupsFileExists(yamlFilename: String) = File(yamlFilename).exists()


}