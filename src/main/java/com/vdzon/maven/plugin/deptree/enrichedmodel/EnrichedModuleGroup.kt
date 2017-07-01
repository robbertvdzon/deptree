package com.vdzon.maven.plugin.deptree.enrichedmodel

data class EnrichedModuleGroup(
        val modulegroup: String,
        var modulegroups:EnrichedModuleGroups? = null,
        val layers: List<EnrichedModuleLayer>
)
