package com.vdzon.maven.plugin.deptree.enrichedmodel

data class EnrichedModule(val name: String,
                          var moduleLayer: EnrichedModuleLayer? = null,
                          var depsTo: List<EnrichedModule> = listOf<EnrichedModule>(),
                          var depsFrom: List<EnrichedModule>  = listOf<EnrichedModule>())
