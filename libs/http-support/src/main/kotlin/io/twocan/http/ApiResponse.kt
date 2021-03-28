package io.twocan.http

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
        val data: T,
)
