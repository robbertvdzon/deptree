package com.vdzon.maven.plugin.deptree.jsonmodel


class ModuleGroups(
        var application: String = "",
        var moduleGroups: List<ModuleGroup> = listOf<ModuleGroup>()
) {
}

