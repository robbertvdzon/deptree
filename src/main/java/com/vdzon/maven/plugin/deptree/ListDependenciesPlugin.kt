package com.vdzon.maven.plugin.deptree


import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.vdzon.maven.plugin.deptree.jsonmodel.*
import org.apache.maven.execution.MavenSession
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugin.MojoFailureException
import org.apache.maven.plugins.annotations.Component
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject
import org.apache.maven.project.ProjectBuilder
import org.apache.maven.project.ProjectBuildingException
import org.apache.maven.repository.RepositorySystem
import java.io.*
import java.util.*


@Mojo(name = "build-dep-tree", defaultPhase = LifecyclePhase.PROCESS_SOURCES, aggregator = false)
class ListDependenciesPlugin : AbstractMojo() {

    @Parameter(defaultValue = "\${project}", readonly = true)
    private val project: MavenProject? = null

    @Parameter(defaultValue = "\${session}", readonly = true)
    private val session: MavenSession? = null

    @Component
    private val repositorySystem: RepositorySystem? = null

    @Component
    private val projectBuilder: ProjectBuilder? = null

    @Parameter(defaultValue = "target", property = "workFolder", required = true)
    private val workFolder: String? = null

    @Parameter(property = "print", required = false)
    private val print: Boolean = false

    @Throws(MojoExecutionException::class, MojoFailureException::class)
    override fun execute() {
        val allDeps = ArrayList<String>()
        processParentAndImportsRecursive(project!!, allDeps)
        writeDeps(allDeps)
        updateArtifactGroups()
    }

    private fun updateArtifactGroups() {
        val artifactGroupService = ArtifactGroupService()
        val yamlFilename = "groups.yaml"
        val artifactGroups: ArtifactGroups = artifactGroupService.getExistsingOrNewArtifactGroups(yamlFilename, artifactGroupService)
        val artifactName = project!!.groupId + ":" + project.artifactId
        val artifactExists = artifactGroups.artifactGroups.any { it.layers.any { it.artifacts.any { it.name == artifactName } } }
        val modifiedGroupsObject = if (!artifactExists) addToUnknownGroup(artifactGroups, artifactName) else artifactGroups
        writeArtifactGroups(yamlFilename, artifactGroupService, modifiedGroupsObject)
    }

    private fun addToUnknownGroup(artifactGroups: ArtifactGroups, artifactName: String): ArtifactGroups {
        val unknownGroupFound = artifactGroups.artifactGroups.find { it.artifactgroup == "unknown" }
        val unknownGroup = if (unknownGroupFound != null) unknownGroupFound else ArtifactGroup("unknown")

        val unknownLayersFound = unknownGroup.layers.find { it.name == "unknown" }
        val unknownLayer = if (unknownLayersFound != null) unknownLayersFound else ArtifactLayer("unknown")

        val modifiedArtifacts = unknownLayer.artifacts.toMutableList();
        modifiedArtifacts.filter { it.name == artifactName }.forEach { modifiedArtifacts.remove(it) }
        modifiedArtifacts.add(Artifact(artifactName))
        val modifiedLayer = ArtifactLayer("unknown", modifiedArtifacts);

        val modifiedLayers = unknownGroup.layers.toMutableList();
        modifiedLayers.filter { it.name == "unknown" }.forEach { modifiedLayers.remove(it) }
        modifiedLayers.add(modifiedLayer)

        val modifiedGroups = artifactGroups.artifactGroups.toMutableList();
        modifiedGroups.filter { it.artifactgroup == "unknown" }.forEach { modifiedGroups.remove(it) }
        modifiedGroups.add(ArtifactGroup("unknown", modifiedLayers))

        val modifiedGroupsObject = ArtifactGroups(artifactGroups.application, modifiedGroups);
        return modifiedGroupsObject
    }

    private fun writeArtifactGroups(yamlFilename: String, artifactGroupService: ArtifactGroupService, artifactGroups: ArtifactGroups) {
        File(yamlFilename).writeText(artifactGroupService.serializeArtifactGroups(artifactGroups), charset = Charsets.UTF_8)
    }


    @Throws(MojoExecutionException::class)
    private fun writeDeps(allDeps: List<String>) {
        File(workFolder!!).mkdirs()
        val filename = workFolder + File.separator + project!!.groupId + "-" + project.artifactId + "-" + project.version + ".deps.yaml"
        val depList = ArrayList<String>();
        for (dep in allDeps) {
            depList.add(dep)
        }
        var artifactDependency = ArtifactDependency()
        artifactDependency.groupId=project.groupId
        artifactDependency.artifactId=project.artifactId
        artifactDependency.version=project.version
        artifactDependency.absolutePath=project.basedir.absolutePath
        artifactDependency.packaging=project.packaging
        artifactDependency.deps=depList

        val mapper = ObjectMapper(YAMLFactory())
        val yaml = mapper.writeValueAsString(artifactDependency)
        File(filename).writeText(yaml, charset = Charsets.UTF_8)
    }

    private fun processParentAndImportsRecursive(currentProject: MavenProject, allDeps: MutableList<String>) {
        processParentRecursive(currentProject.parent, allDeps)
        displayManagedDependencies(currentProject, allDeps)
        processImportsRecursive(currentProject, allDeps)
    }

    private fun displayManagedDependencies(currentProject: MavenProject, allDeps: MutableList<String>) {
        for (dependency in currentProject.dependencies) {
            allDeps.add(dependency.groupId + ":" + dependency.artifactId)
        }
    }

    private fun processParentRecursive(parent: MavenProject?, allDeps: MutableList<String>): Boolean {
        if (parent != null) {
            allDeps.add(parent.groupId + ":" + parent.artifactId)
            processParentAndImportsRecursive(parent, allDeps)
        }
        return parent != null
    }

    private fun processImportsRecursive(currentProject: MavenProject, allDeps: MutableList<String>) {
        for (dependency in currentProject.originalModel.dependencies) {
            if ("import" == dependency.scope && "pom" == dependency.type) {
                var mavenProject: MavenProject
                try {
                    try {
                        mavenProject = getMavenProject(currentProject, dependency.groupId, dependency.artifactId,
                                dependency.version)
                    } catch (e: ProjectBuildingException) {
                        // try again with fixed project.version, this fails on the gwt artifact
                        var version = dependency.version
                        if (version == "\${project.version}") {
                            version = currentProject.version
                        }
                        mavenProject = getMavenProject(currentProject, dependency.groupId, dependency.artifactId, version)
                    }

                    allDeps.add(dependency.groupId + ":" + dependency.artifactId)
                    processParentAndImportsRecursive(mavenProject, allDeps)
                } catch (e: ProjectBuildingException) {
                    e.printStackTrace()
                    throw RuntimeException(e)
                }

            }
        }
    }

    @Throws(ProjectBuildingException::class)
    private fun getMavenProject(currentProject: MavenProject, groupId: String, artifactId: String, version: String): MavenProject {
        val resolvedVersion = resolveVersionProperty(currentProject, version)
        val pomArtifact = repositorySystem!!.createProjectArtifact(groupId, artifactId, resolvedVersion)
        return projectBuilder!!.build(pomArtifact, session!!.projectBuildingRequest).project

    }

    private fun resolveVersionProperty(currentProject: MavenProject, version: String): String {
        if (isVersionSetAsProperty(version)) {
            val propertyName = version.replace("$", "").replace("{", "").replace("}", "")
            return findPropertyRecursively(currentProject, propertyName)
        }

        return version
    }

    private fun findPropertyRecursively(mavenProject: MavenProject, propertyName: String): String {
        val properties = mavenProject.properties
        if (properties != null) {
            val propertyValue = properties[propertyName] as String
            if (propertyValue != null) {
                return propertyValue
            }
        }

        val parent = mavenProject.parent
        if (parent != null) {
            return findPropertyRecursively(parent, propertyName)
        }

        return propertyName
    }

    private fun isVersionSetAsProperty(version: String): Boolean {
        return version.contains("\${")
    }

}
