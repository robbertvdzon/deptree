package com.vdzon.maven.plugin.deptree.resource


import com.vdzon.maven.plugin.deptree.DepServer
import com.vdzon.maven.plugin.deptree.dto.*
import com.vdzon.maven.plugin.deptree.enrichedmodel.EnrichedArtifact
import com.vdzon.maven.plugin.deptree.enrichedmodel.EnrichedArtifactModules
import com.vdzon.maven.plugin.deptree.jsonmodel.ModuleDto
import com.vdzon.maven.plugin.deptree.jsonmodel.ArtifactDto

class Builder {

    companion object Helper {

        fun buildModel(): ModelDto {
            val data: EnrichedArtifactModules? = DepServer.data
            if (data != null) {
                val artifacts: List<EnrichedArtifact> = data
                        .artifactModules
                        .flatMap {
                            it.layers
                        }
                        .flatMap {
                            it.artifacts
                        }

                val modulesDto = data
                        .artifactModules
                        .map {
                            ModuleDto(it.artifactmodule)
                        }

                val artifactsDto = artifacts.map {
                    ArtifactDto(name = it.name, module = it.artifactLayer!!.artifactModule!!.artifactmodule)
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

                val depArtifactModule = artifacts
                        .flatMap {
                            artifactFrom ->
                            artifactFrom.depsTo
                                    .map {
                                        artifactTo ->
                                        DepArtifactModuleDto(from = artifactFrom.name, to = artifactTo.artifactLayer!!.artifactModule!!.artifactmodule)
                                    }
                        }
                        .distinctBy { dep -> dep.from + dep.to }
                        .filter { dep -> dep.from != dep.to }

                val depModuleArtifact = artifacts
                        .flatMap {
                            artifactFrom ->
                            artifactFrom.depsTo
                                    .map {
                                        artifactTo ->
                                        DepModuleArtifactDto(from = artifactFrom.artifactLayer!!.artifactModule!!.artifactmodule, to = artifactTo.name)
                                    }
                        }
                        .distinctBy { dep -> dep.from + dep.to }
                        .filter { dep -> dep.from != dep.to }

                val depModuleModule = artifacts
                        .flatMap {
                            artifactFrom ->
                            artifactFrom.depsTo
                                    .map {
                                        artifactTo ->
                                        DepModuleModuleDto(from = artifactFrom.artifactLayer!!.artifactModule!!.artifactmodule, to = artifactTo.artifactLayer!!.artifactModule!!.artifactmodule)
                                    }
                        }
                        .distinctBy { dep -> dep.from + dep.to }
                        .filter { dep -> dep.from != dep.to }


                return ModelDto(modules = modulesDto
                        , artifacts = artifactsDto
                        , depArtifactArtifacts = depArtifactArtifact
                        , depArtifactModules = depArtifactModule
                        , depModuleArtifacts = depModuleArtifact
                        , depModuleModules = depModuleModule
                )
            }
            return ModelDto();
        }
    }
}