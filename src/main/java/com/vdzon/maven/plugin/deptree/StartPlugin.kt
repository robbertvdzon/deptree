package com.vdzon.maven.plugin.deptree


import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugins.annotations.Mojo
import java.awt.Desktop
import java.net.URI


@Mojo(name = "start")
class StartPlugin : AbstractMojo() {

    @Throws(MojoExecutionException::class)
    override fun execute() {
        Desktop.getDesktop().browse(URI("http://localhost:8080"));
        DepServer().start()
    }


}
