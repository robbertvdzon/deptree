package com.vdzon.maven.plugin.deptree.dto;

import com.vdzon.maven.plugin.deptree.jsonmodel.ModuleDto
import com.vdzon.maven.plugin.deptree.jsonmodel.GroupDto
import java.util.ArrayList

class ModelDto (
        val modules:List<ModuleDto> = ArrayList(),
        val groups:List<GroupDto> = ArrayList(),
        val depGroupGroups:List<DepGroupGroupDto> = ArrayList(),
        val depGroupModules:List<DepGroupModuleDto> = ArrayList(),
        val depModuleGroups:List<DepModuleGroupDto> = ArrayList(),
        val depModuleModules:List<DepModuleModuleDto> = ArrayList()
)