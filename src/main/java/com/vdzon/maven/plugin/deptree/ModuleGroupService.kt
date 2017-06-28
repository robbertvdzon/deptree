package com.vdzon.maven.plugin.deptree

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.vdzon.maven.plugin.deptree.model.ModuleGroups


class ModuleGroupService {
    fun serializeModuleGroups(moduleGroups: ModuleGroups): String {
        val mapper = ObjectMapper(YAMLFactory())
        return mapper.writeValueAsString(moduleGroups)
    }

    fun deserializeModuleGroups(yaml: String): ModuleGroups {
        val mapper = ObjectMapper(YAMLFactory())
        return mapper.readValue(yaml, ModuleGroups::class.java)
    }


}