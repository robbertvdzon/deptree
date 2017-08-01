package com.vdzon.maven.plugin.deptree.jsonmodel

class ArtifactGroup(
        var artifactgroup: String = "",
        var layers: List<ArtifactLayer> = listOf<ArtifactLayer>()
){
}

