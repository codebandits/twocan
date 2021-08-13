package io.twocan.birds

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant
import java.util.*

data class Bird(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val lastFlight: Instant?,
) {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    val name = "$firstName $lastName"
}
