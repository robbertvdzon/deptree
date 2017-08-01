package com.vdzon.maven.plugin.deptree.enrichedmodel

data class EnrichedArtifactLayer(
        val name: String = "",
        var artifactGroup: EnrichedArtifactGroup? = null,
        val artifacts: List<EnrichedArtifact>  = listOf<EnrichedArtifact>()
)