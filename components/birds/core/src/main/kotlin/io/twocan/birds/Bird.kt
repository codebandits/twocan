package io.twocan.birds

import io.twocan.serialization.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Bird(
        @Serializable(with = UUIDSerializer::class)
        val id: UUID,
        val firstName: String,
        val lastName: String,
)
