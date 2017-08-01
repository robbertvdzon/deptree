package com.vdzon.maven.plugin.deptree.jsonmodel

import java.util.ArrayList

class ArtifactDependency {
    var moduleId: String = ""
    var artifactId: String = ""
    var version: String = ""
    var absolutePath: String = ""
    var packaging: String = ""
    var deps: List<String> = ArrayList()
}

