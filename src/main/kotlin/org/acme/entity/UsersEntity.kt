package org.acme.entity

import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import io.quarkus.runtime.annotations.RegisterForReflection
import javax.persistence.Entity
import javax.persistence.*

@Entity
@Table(name = "users")
@NamedQuery(name = "Users.findAll", query = "SELECT users FROM  UsersEntity users")
@NamedQuery(name = "Users.findByName", query = "SELECT users from UsersEntity users WHERE users.name = :name")
@NamedQuery(name = "Users.findById", query = "SELECT users from UsersEntity users WHERE users.id = :id")
@RegisterForReflection
class UsersEntity {
    @Id
    @SequenceGenerator(name = "userIdSeq", sequenceName = "user_id_seq", allocationSize = 1, initialValue = 1)
    @GeneratedValue(generator = "userIdSeq")
    var id: Long = 0

    @Column(name = "name", length = 40, nullable = false, insertable = true, updatable = true)
    var name: String = ""

    @Column(name = "city", length = 30, nullable = false, insertable = true, updatable = true)
    var city: String = ""

    @Column(name = "age", length = 4, insertable = true, updatable = true)
    var age: Int = 0

    fun update(other: UsersEntity) : UsersEntity {
        this.name = other.name
        this.city = other.city
        this.age = other.age
        return this
    }
}