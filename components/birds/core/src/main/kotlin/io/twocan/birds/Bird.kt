package io.twocan.birds

import java.time.Instant
import java.util.*

data class Bird(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val lastFlight: Instant?,
) {
    val name get() = "$firstName $lastName"
}
