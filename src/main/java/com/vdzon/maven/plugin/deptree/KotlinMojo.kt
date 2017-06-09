package com.vdzon.maven.plugin.deptree


import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter

@Mojo(name = "init")
class KotlinMojo : AbstractMojo() {

    @Parameter
    private val foo: String? = null

    @Parameter
    private val bar: String? = null

    @Throws(MojoExecutionException::class)
    override fun execute() {
        log.info("Build dep tree")
    }
}
