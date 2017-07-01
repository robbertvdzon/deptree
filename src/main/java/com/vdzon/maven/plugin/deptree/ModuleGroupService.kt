package com.vdzon.maven.plugin.deptree

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.vdzon.maven.plugin.deptree.model.ModuleGroups
import java.io.File


class ModuleGroupService {
    fun serializeModuleGroups(moduleGroups: ModuleGroups): String {
        val mapper = ObjectMapper(YAMLFactory())
        return mapper.writeValueAsString(moduleGroups)
    }

    fun deserializeModuleGroups(yaml: String): ModuleGroups {
        val mapper = ObjectMapper(YAMLFactory())
        return mapper.readValue(yaml, ModuleGroups::class.java)
    }

    fun getExistsingOrNewModuleGroups(yamlFilename: String, moduleGroupService: ModuleGroupService) = if (moduleGroupsFileExists(yamlFilename)) readModuleGroups(moduleGroupService, yamlFilename) else ModuleGroups()

    private fun readModuleGroups(moduleGroupService: ModuleGroupService, yamlFilename: String) = moduleGroupService.deserializeModuleGroups(File(yamlFilename).readText(charset = Charsets.UTF_8))

    private fun moduleGroupsFileExists(yamlFilename: String) = File(yamlFilename).exists()


}