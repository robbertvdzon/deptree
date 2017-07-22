package com.vdzon.maven.plugin.deptree.resource


import com.vdzon.maven.plugin.deptree.DepServer
import com.vdzon.maven.plugin.deptree.dto.*
import com.vdzon.maven.plugin.deptree.enrichedmodel.EnrichedModule
import com.vdzon.maven.plugin.deptree.enrichedmodel.EnrichedModuleGroups
import com.vdzon.maven.plugin.deptree.model.GroupDto
import com.vdzon.maven.plugin.deptree.model.ModuleDto
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("modulegroup")
class TestResource {

    class ModuleGroupsToModuleGroupModel(
            val modules: List<ModuleGroupToModuleGroupModel> = listOf()
    )

    class ModuleGroupToModuleGroupModel(
            val name: String = "",
            val dep: List<String>? = listOf()
    )

    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    fun listGroupDeps(): ModuleGroupsToModuleGroupModel {
        val data: EnrichedModuleGroups? = DepServer.data
        if (data != null) {
            val model = ModuleGroupsToModuleGroupModel(data.moduleGroups.map {
                val modulegroup = it.modulegroup
                ModuleGroupToModuleGroupModel(
                        it.modulegroup,
                        it.layers.flatMap {
                            it.modules.flatMap {
                                it.depsTo.map {
                                    it.moduleLayer?.moduleGroup?.modulegroup ?: "Unknown"
                                }
                            }
                        }.distinct().filter { it != modulegroup }
                )
            })
            return model
        }


        return ModuleGroupsToModuleGroupModel()
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    fun find(@PathParam("id") id: Long): String {
        return "Hallo $id!"
    }

    @GET
    @Path("json")
    @Produces(MediaType.APPLICATION_JSON)
    fun listJson(): ModelDto {
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
                    .filter { dep -> dep.from != dep.to  }

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
                    .filter { dep -> dep.from != dep.to  }

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
                    .filter { dep -> dep.from != dep.to  }

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
                    .filter { dep -> dep.from != dep.to  }


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