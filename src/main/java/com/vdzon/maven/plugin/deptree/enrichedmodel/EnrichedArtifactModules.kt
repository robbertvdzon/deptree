package com.vdzon.maven.plugin.deptree.enrichedmodel


data class EnrichedArtifactModules(
        val application: String,
        val artifactModules: List<EnrichedArtifactModule>
)
