package com.vdzon.maven.plugin.deptree.dto;

import com.vdzon.maven.plugin.deptree.jsonmodel.ArtifactDto
import com.vdzon.maven.plugin.deptree.jsonmodel.GroupDto
import java.util.ArrayList

class ModelDto (
        val artifacts:List<ArtifactDto> = ArrayList(),
        val groups:List<GroupDto> = ArrayList(),
        val depGroupGroups:List<DepGroupGroupDto> = ArrayList(),
        val depGroupArtifacts:List<DepGroupArtifactDto> = ArrayList(),
        val depArtifactGroups:List<DepArtifactGroupDto> = ArrayList(),
        val depArtifactArtifacts:List<DepArtifactArtifactDto> = ArrayList()
)