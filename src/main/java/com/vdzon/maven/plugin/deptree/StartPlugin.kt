package com.vdzon.maven.plugin.deptree


import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import java.awt.Desktop
import java.io.File
import java.net.URI


@Mojo(name = "start", defaultPhase = LifecyclePhase.PROCESS_SOURCES, aggregator = true)
class StartPlugin : AbstractMojo() {

    @Throws(MojoExecutionException::class)
    override fun execute() {
        DepServer().start()
        Desktop.getDesktop().browse(URI("file:///"+File("target","dependencies.html").absolutePath.replace("\\","/")));
    }


}
