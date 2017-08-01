package com.vdzon.maven.plugin.deptree.enrichedmodel

data class EnrichedArtifactGroup(
        val artifactgroup: String,
        var artifactgroups: EnrichedArtifactGroups? = null,
        val layers: List<EnrichedArtifactLayer>
)
