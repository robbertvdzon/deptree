package com.vdzon.maven.plugin.deptree.model


class ModuleGroups(
        var application: String = "",
        var moduleGroups: List<ModuleGroup> = listOf<ModuleGroup>()
) {
}

