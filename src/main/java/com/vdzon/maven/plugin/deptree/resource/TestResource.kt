package com.vdzon.maven.plugin.deptree.resource


import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path("customers")
class TestResource {
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    fun find(@PathParam("id") id: Long): String {
        return "Hallo $id!"
    }


}