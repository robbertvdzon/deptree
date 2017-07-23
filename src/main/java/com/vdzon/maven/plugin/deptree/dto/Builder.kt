package com.vdzon.maven.plugin.deptree.resource


import com.vdzon.maven.plugin.deptree.DepServer
import com.vdzon.maven.plugin.deptree.dto.*
import com.vdzon.maven.plugin.deptree.enrichedmodel.EnrichedModule
import com.vdzon.maven.plugin.deptree.enrichedmodel.EnrichedModuleGroups
import com.vdzon.maven.plugin.deptree.model.GroupDto
import com.vdzon.maven.plugin.deptree.model.ModuleDto

class Builder {

    companion object Helper {

        fun buildModel(): ModelDto {
            val data: EnrichedModuleGroups? = DepServer.data
            if (data != null) {
                val modules: List<EnrichedModule> = data
                        .moduleGroups
                        .flatMap {
                            it.layers
                        }
                        .flatMap {
                            it.modules
                        }

                val groupsDto = data
                        .moduleGroups
                        .map {
                            GroupDto(it.modulegroup)
                        }

                val modulesDto = modules.map {
                    ModuleDto(name = it.name, group = it.moduleLayer!!.moduleGroup!!.modulegroup)
                }

                val depModuleModule = modules
                        .flatMap {
                            moduleFrom ->
                            moduleFrom.depsTo
                                    .map {
                                        moduleTo ->
                                        DepModuleModuleDto(from = moduleFrom.name, to = moduleTo.name)
                                    }
                        }
                        .distinctBy { dep -> dep.from + dep.to }
                        .filter { dep -> dep.from != dep.to }

                val depModuleGroup = modules
                        .flatMap {
                            moduleFrom ->
                            moduleFrom.depsTo
                                    .map {
                                        moduleTo ->
                                        DepModuleGroupDto(from = moduleFrom.name, to = moduleTo.moduleLayer!!.moduleGroup!!.modulegroup)
                                    }
                        }
                        .distinctBy { dep -> dep.from + dep.to }
                        .filter { dep -> dep.from != dep.to }

                val depGroupModule = modules
                        .flatMap {
                            moduleFrom ->
                            moduleFrom.depsTo
                                    .map {
                                        moduleTo ->
                                        DepGroupModuleDto(from = moduleFrom.moduleLayer!!.moduleGroup!!.modulegroup, to = moduleTo.name)
                                    }
                        }
                        .distinctBy { dep -> dep.from + dep.to }
                        .filter { dep -> dep.from != dep.to }

                val depGroupGroup = modules
                        .flatMap {
                            moduleFrom ->
                            moduleFrom.depsTo
                                    .map {
                                        moduleTo ->
                                        DepGroupGroupDto(from = moduleFrom.moduleLayer!!.moduleGroup!!.modulegroup, to = moduleTo.moduleLayer!!.moduleGroup!!.modulegroup)
                                    }
                        }
                        .distinctBy { dep -> dep.from + dep.to }
                        .filter { dep -> dep.from != dep.to }


                return ModelDto(groups = groupsDto
                        , modules = modulesDto
                        , depModuleModules = depModuleModule
                        , depModuleGroups = depModuleGroup
                        , depGroupModules = depGroupModule
                        , depGroupGroups = depGroupGroup
                )
            }
            return ModelDto();
        }
    }
}