package com.vdzon.maven.plugin.deptree.jsonmodel


class ArtifactGroups(
        var application: String = "",
        var artifactGroups: List<ArtifactGroup> = listOf<ArtifactGroup>()
) {
}

