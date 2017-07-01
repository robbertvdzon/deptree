package com.vdzon.maven.plugin.deptree.model

class ModuleGroup(
        var modulegroup: String = "",
        var layers: List<ModuleLayer> = listOf<ModuleLayer>()
){
}

