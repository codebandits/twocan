package io.twocan.http

import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@type")
sealed class GetResponse<out T> {
    abstract val status: String

    data class Ok<out T>(val data: T) : GetResponse<T>() {
        override val status = "OK"
    }

    data class NotFound<out T>(val message: String) : GetResponse<T>() {
        override val status = "NOT_FOUND"
    }

//    data class Unauthorized<out T>(val message: String) : GetResponse<T>() {
//        override val status = "UNAUTHORIZED"
//    }
//
//    data class Forbidden<out T>(val message: String) : GetResponse<T>() {
//        override val status = "FORBIDDEN"
//    }
//
//    data class InternalServerError<out T>(val message: String) : GetResponse<T>() {
//        override val status = "INTERNAL_SERVER_ERROR"
//    }
}
