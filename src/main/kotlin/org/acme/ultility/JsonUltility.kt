package org.acme.ultility


import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

object JsonUltility {
    fun createMapper() = jacksonObjectMapper().apply {
        propertyNamingStrategy = PropertyNamingStrategy.LOWER_CAMEL_CASE
        setSerializationInclusion(JsonInclude.Include.NON_NULL)
        registerModule(JavaTimeModule())
    }

    fun <T> jsonToString(jsonObject: T) : String = createMapper().writeValueAsString(jsonObject)
    inline fun <reified T> stringToJson(value: String) = createMapper().readValue<T>(value)
}