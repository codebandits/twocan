package io.twocan.identity

import io.twocan.http.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Session(
        @Serializable(with = UUIDSerializer::class)
        val userId: UUID,
        val name: String,
)
