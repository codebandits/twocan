package io.twocan.http

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
        val data: T
)

fun <T : Any> T.toApiResponse(): ApiResponse<T> = ApiResponse(data = this)
