package com.vdzon.maven.plugin.deptree.resource


import com.vdzon.maven.plugin.deptree.DepServer
import com.vdzon.maven.plugin.deptree.dto.*
import com.vdzon.maven.plugin.deptree.enrichedmodel.EnrichedArtifact
import com.vdzon.maven.plugin.deptree.enrichedmodel.EnrichedArtifactGroups
import com.vdzon.maven.plugin.deptree.jsonmodel.GroupDto
import com.vdzon.maven.plugin.deptree.jsonmodel.ArtifactDto

class Builder {

    companion object Helper {

        fun buildModel(): ModelDto {
            val data: EnrichedArtifactGroups? = DepServer.data
            if (data != null) {
                val artifacts: List<EnrichedArtifact> = data
                        .artifactGroups
                        .flatMap {
                            it.layers
                        }
                        .flatMap {
                            it.artifacts
                        }

                val groupsDto = data
                        .artifactGroups
                        .map {
                            GroupDto(it.artifactgroup)
                        }

                val artifactsDto = artifacts.map {
                    ArtifactDto(name = it.name, group = it.artifactLayer!!.artifactGroup!!.artifactgroup)
                }

                val depArtifactArtifact = artifacts
                        .flatMap {
                            artifactFrom ->
                            artifactFrom.depsTo
                                    .map {
                                        artifactTo ->
                                        DepArtifactArtifactDto(from = artifactFrom.name, to = artifactTo.name)
                                    }
                        }
                        .distinctBy { dep -> dep.from + dep.to }
                        .filter { dep -> dep.from != dep.to }

                val depArtifactGroup = artifacts
                        .flatMap {
                            artifactFrom ->
                            artifactFrom.depsTo
                                    .map {
                                        artifactTo ->
                                        DepArtifactGroupDto(from = artifactFrom.name, to = artifactTo.artifactLayer!!.artifactGroup!!.artifactgroup)
                                    }
                        }
                        .distinctBy { dep -> dep.from + dep.to }
                        .filter { dep -> dep.from != dep.to }

                val depGroupArtifact = artifacts
                        .flatMap {
                            artifactFrom ->
                            artifactFrom.depsTo
                                    .map {
                                        artifactTo ->
                                        DepGroupArtifactDto(from = artifactFrom.artifactLayer!!.artifactGroup!!.artifactgroup, to = artifactTo.name)
                                    }
                        }
                        .distinctBy { dep -> dep.from + dep.to }
                        .filter { dep -> dep.from != dep.to }

                val depGroupGroup = artifacts
                        .flatMap {
                            artifactFrom ->
                            artifactFrom.depsTo
                                    .map {
                                        artifactTo ->
                                        DepGroupGroupDto(from = artifactFrom.artifactLayer!!.artifactGroup!!.artifactgroup, to = artifactTo.artifactLayer!!.artifactGroup!!.artifactgroup)
                                    }
                        }
                        .distinctBy { dep -> dep.from + dep.to }
                        .filter { dep -> dep.from != dep.to }


                return ModelDto(groups = groupsDto
                        , artifacts = artifactsDto
                        , depArtifactArtifacts = depArtifactArtifact
                        , depArtifactGroups = depArtifactGroup
                        , depGroupArtifacts = depGroupArtifact
                        , depGroupGroups = depGroupGroup
                )
            }
            return ModelDto();
        }
    }
}