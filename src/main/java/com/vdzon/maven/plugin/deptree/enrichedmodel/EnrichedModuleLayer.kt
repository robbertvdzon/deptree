package com.vdzon.maven.plugin.deptree.enrichedmodel

data class EnrichedModuleLayer(
        val name: String = "",
        var moduleGroup: EnrichedModuleGroup? = null,
        val modules : List<EnrichedModule>  = listOf<EnrichedModule>()
)