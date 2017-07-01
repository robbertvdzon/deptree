package com.vdzon.maven.plugin.deptree.enrichedmodel


data class EnrichedModuleGroups(
        val application: String,
        val moduleGroups: List<EnrichedModuleGroup>
)
