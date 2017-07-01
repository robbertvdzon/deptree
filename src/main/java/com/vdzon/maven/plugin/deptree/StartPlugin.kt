package com.vdzon.maven.plugin.deptree


import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugins.annotations.Mojo


@Mojo(name = "start")
class StartPlugin : AbstractMojo() {

    @Throws(MojoExecutionException::class)
    override fun execute() {
        DepServer().start()
    }


}
