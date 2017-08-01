package com.vdzon.maven.plugin.deptree.enrichedmodel

data class EnrichedArtifactModule(
        val artifactmodule: String,
        var artifactmodules: EnrichedArtifactModules? = null,
        val layers: List<EnrichedArtifactLayer>
)
