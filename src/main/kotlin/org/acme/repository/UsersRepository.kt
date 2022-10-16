package org.acme.repository

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import io.quarkus.panache.common.Sort
import io.smallrye.mutiny.Uni
import org.acme.entity.UsersEntity
import org.acme.ultility.GeneralUltility
import org.acme.ultility.JsonUltility
import java.util.Optional
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.persistence.EntityManager
import javax.persistence.TypedQuery
import javax.ws.rs.core.Response

@ApplicationScoped
class UsersRepository {
    @Inject
    lateinit var entityMgr: EntityManager

    private fun createUserItem(
        userName: String,
        userCity: String,
        userAge: Int,
        usersEntity: UsersEntity
    ): UsersEntity {
        return usersEntity.run {
            this.name = userName
            this.city = userCity
            this.age = userAge
            this
        }
    }

    private fun namedQueryFindAll(): TypedQuery<UsersEntity> {
        return entityMgr.createNamedQuery("Users.findAll", UsersEntity::class.java)
    }

    fun listAll(): Uni<Response> {
        return GeneralUltility.uniDataItemList(namedQueryFindAll())
            .map { Response.ok().entity(JsonUltility.jsonToString(it)).status(200).build() }
    }

    private fun namedQueryFindByName(name: String): TypedQuery<UsersEntity> {
        return entityMgr.createNamedQuery("Users.findByName", UsersEntity::class.java)
            .apply { setParameter("name", name) }
    }

    private fun namedQueryFindById(id: Long): TypedQuery<UsersEntity> {
        return entityMgr.createNamedQuery("Users.findById", UsersEntity::class.java)
            .apply { setParameter("id", id) }
    }


    private fun checkUsersById(id: Long): Uni<Optional<UsersEntity>> {
        return Uni.createFrom().item(namedQueryFindById(id))
            .map { typeQuery -> typeQuery.resultStream.findFirst() }
    }

    private fun checkUsersNotExists(id: Long): Uni<Boolean> {
        return checkUsersById(id).map { item -> item.isPresent }
    }

    private fun checkUserExists(id: Long): Uni<UsersEntity> {
        return checkUsersById(id)
            .flatMap { item ->
                if (item.isPresent)
                    Uni.createFrom().item(item.get())
                else
                    Uni.createFrom().failure(Throwable("User not exists!"))
            }
    }

    private fun createResponseFailure(action: String, errMsg: String): Response {
        val strJson = "{ \"action\" : \"${action}\", \"status\" : \"Failure\", \"errorMessage\" : \"${errMsg}\"}"
        return Response.ok().entity(strJson).build()
    }

    fun createUser(usersEntity: UsersEntity): Uni<Response> {
        fun isValidUser(flag: Boolean): Uni<UsersEntity> {
            return if (flag.not())
                Uni.createFrom().item(createUserItem(usersEntity.name, usersEntity.city, usersEntity.age, usersEntity))
            else
                Uni.createFrom().failure(Throwable("User already exists!"))
        }

        return checkUsersNotExists(usersEntity.id).flatMap { isExist -> isValidUser(isExist) }
            .map { item -> entityMgr.persist(item) }.flatMap { checkUserExists(usersEntity.id) }
            .map { Response.ok().entity(JsonUltility.jsonToString(it)).build() }.onFailure()
            .recoverWithItem { throwable -> createResponseFailure("Add User", throwable.message ?: "Unknown Error") }
    }
}