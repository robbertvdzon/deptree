package com.vdzon.maven.plugin.deptree.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties("deps")
class Module (val name:String = "",
              var deps: List<String> = listOf<String>()){

}
