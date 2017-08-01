package com.vdzon.maven.plugin.deptree.enrichedmodel


data class EnrichedArtifactGroups(
        val application: String,
        val artifactGroups: List<EnrichedArtifactGroup>
)
