package com.vdzon.maven.plugin.deptree.model

class ModuleGroup(
        var name: String = "",
        var layer: List<ModuleLayer> = listOf<ModuleLayer>()
){
}

