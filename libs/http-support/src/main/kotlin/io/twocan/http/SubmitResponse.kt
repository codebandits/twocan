package io.twocan.http

import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.util.*

typealias ResponseErrors = Map<String, String>

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@type")
sealed class SubmitResponse {
    abstract val status: String

    // I would prefer to swap this out for an object (singleton) but objects can't seem to be deserialized as part of sealed class
    class Accepted : SubmitResponse() {
        override val status = "ACCEPTED"

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Accepted

            if (status != other.status) return false

            return true
        }

        override fun hashCode(): Int {
            return status.hashCode()
        }
    }

    data class Created(val id: UUID) : SubmitResponse() {
        override val status = "CREATED"
    }

    data class BadRequestErrors(val errors: ResponseErrors) : SubmitResponse() {
        override val status = "BAD_REQUEST"
    }

//    data class BadRequestMessage(val message: String) : SubmitResponse() {
//        override val status = "BAD_REQUEST"
//    }
//
//    data class Unauthorized(val message: String) : SubmitResponse() {
//        override val status = "UNAUTHORIZED"
//    }
//
//    data class Forbidden(val message: String) : SubmitResponse() {
//        override val status = "FORBIDDEN"
//    }
//
//    data class InternalServerError(val message: String) : SubmitResponse() {
//        override val status = "INTERNAL_SERVER_ERROR"
//    }
}
