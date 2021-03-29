package io.twocan.identity

import io.twocan.serialization.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Session(
        @Serializable(with = UUIDSerializer::class)
        val id: UUID,
        val user: User,
)
