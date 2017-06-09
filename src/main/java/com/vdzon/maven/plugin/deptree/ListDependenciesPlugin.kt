package com.vdzon.maven.plugin.deptree


import org.apache.maven.artifact.Artifact
import org.apache.maven.execution.MavenSession
import org.apache.maven.model.Dependency
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

import java.io.File
import java.io.FileNotFoundException
import java.io.PrintWriter
import java.io.UnsupportedEncodingException
import java.util.ArrayList
import java.util.Properties

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
    }

    @Throws(MojoExecutionException::class)
    private fun writeDeps(allDeps: List<String>) {
        File(workFolder!!).mkdirs()
        val filename = workFolder + File.separator + project!!.groupId + "-" + project.artifactId + "-" + project.version +".deps"

        var writer: PrintWriter? = null
        try {
            writer = PrintWriter(filename, "UTF-8")
            writer.println(project.groupId)
            writer.println(project.artifactId)
            writer.println(project.version)
            writer.println(project.basedir.absolutePath)
            writer.println(project.packaging)
            for (dep in allDeps) {
                writer.println(dep)
                if (print) {
                    log.info(dep)
                }
            }
            writer.close()
        } catch (e: FileNotFoundException) {
            throw MojoExecutionException("Eception writing dep file", e)
        } catch (e2: UnsupportedEncodingException) {
            throw MojoExecutionException("Eception writing dep file", e2)
        }

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
                        // try again with fixed project.version, this fails on the gwt module
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
