package com.vdzon.maven.plugin.deptree.enrichedmodel

data class EnrichedArtifact(val name: String,
                            var artifactLayer: EnrichedArtifactLayer? = null,
                            var depsTo: List<EnrichedArtifact> = listOf<EnrichedArtifact>(),
                            var depsFrom: List<EnrichedArtifact>  = listOf<EnrichedArtifact>())
