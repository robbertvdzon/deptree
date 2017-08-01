package com.vdzon.maven.plugin.deptree.dto;

import com.vdzon.maven.plugin.deptree.jsonmodel.ArtifactDto
import com.vdzon.maven.plugin.deptree.jsonmodel.ModuleDto
import java.util.ArrayList

class ModelDto (
        val artifacts:List<ArtifactDto> = ArrayList(),
        val modules:List<ModuleDto> = ArrayList(),
        val depModuleModules:List<DepModuleModuleDto> = ArrayList(),
        val depModuleArtifacts:List<DepModuleArtifactDto> = ArrayList(),
        val depArtifactModules:List<DepArtifactModuleDto> = ArrayList(),
        val depArtifactArtifacts:List<DepArtifactArtifactDto> = ArrayList()
)