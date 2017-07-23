package com.vdzon.maven.plugin.deptree.jsonmodel

import java.util.ArrayList

class ModuleDependency {
    var groupId: String = ""
    var artifactId: String = ""
    var version: String = ""
    var absolutePath: String = ""
    var packaging: String = ""
    var deps: List<String> = ArrayList()
}

