package com.vdzon.maven.plugin.deptree

import com.vdzon.maven.plugin.deptree.model.Module
import com.vdzon.maven.plugin.deptree.model.ModuleGroup
import com.vdzon.maven.plugin.deptree.model.ModuleGroups
import com.vdzon.maven.plugin.deptree.model.ModuleLayer
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test


class ModuleGroupServiceTest {

    @Test
    fun test1() {
        val module1 = Module("module1")
        val module2 = Module("module2")
        val module3 = Module("module3")
        val module4 = Module("module4")
        val module5 = Module("module5")
        val layer1 = ModuleLayer("storage", listOf<Module>(module1, module2))
        val layer2 = ModuleLayer("model", listOf<Module>(module3))
        val layer3 = ModuleLayer("storage", listOf<Module>(module4))
        val layer4 = ModuleLayer("usercases", listOf<Module>(module5))
        val moduleGroup1 = ModuleGroup("group1", listOf<ModuleLayer>(layer1, layer2))
        val moduleGroup2 = ModuleGroup("group2", listOf<ModuleLayer>(layer3, layer4))
        var moduleGroups = ModuleGroups("myApp", listOf<ModuleGroup>(moduleGroup1, moduleGroup2))

        val moduleGroupService = ModuleGroupService()
        val yaml = moduleGroupService.serializeModuleGroups(moduleGroups)

        val expected = """---
application: "myApp"
moduleGroups:
- name: "group1"
  layer:
  - name: "storage"
    modules:
    - name: "module1"
    - name: "module2"
  - name: "model"
    modules:
    - name: "module3"
- name: "group2"
  layer:
  - name: "storage"
    modules:
    - name: "module4"
  - name: "usercases"
    modules:
    - name: "module5"
"""
        assertThat(yaml).isEqualTo(expected)
    }

    @Test
    fun test2() {
        val yaml = """---
application: "myApp"
moduleGroups:
- name: "group1"
  layer:
  - name: "storage"
    modules:
    - name: "module1"
    - name: "module2"
  - name: "model"
    modules:
    - name: "module3"
- name: "group2"
  layer:
  - name: "storage"
    modules:
    - name: "module4"
  - name: "usercases"
    modules:
    - name: "module5"
"""

        val moduleGroupService = ModuleGroupService()
        val moduleGroups: ModuleGroups = moduleGroupService.deserializeModuleGroups(yaml)
        assertThat(moduleGroups.moduleGroups.size).isEqualTo(2)


    }

}