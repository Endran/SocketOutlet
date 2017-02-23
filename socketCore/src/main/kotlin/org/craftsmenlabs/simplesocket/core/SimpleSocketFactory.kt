package org.craftsmenlabs.simplesocket.core

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature

//
//open class SimpleSocketFactory {
//
//    fun objectMapper(): ObjectMapper {
//        val mapper = ObjectMapper()
//        mapper.findAndRegisterModules()
//
//        val javaTimeModule = JavaTimeModule()
//        mapper.registerModule(javaTimeModule)
//
//        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
//        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
//        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
//
//        return mapper
//    }
//
//    fun outletRegistry(): OutletRegistry {
//        return OutletRegistry()
//    }
//}

fun ObjectMapper.initForSimpleSockets(): ObjectMapper {
    this.findAndRegisterModules()

    setSerializationInclusion(JsonInclude.Include.NON_NULL)
    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

    return this
}
