package org.acme

import io.smallrye.common.annotation.Blocking
import io.smallrye.mutiny.Uni
import org.acme.entity.UsersEntity
import org.acme.repository.UsersRepository
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.transaction.Transactional
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("users")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
class UsersResource(var repository: UsersRepository) {
    @GET
    @Blocking
    @Path("/list")
    fun get(): Uni<Response> = repository.listAll();

    @POST @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Blocking
    @Path("/create")
    fun create(usersEntity: UsersEntity) = repository.createUser(usersEntity)
}