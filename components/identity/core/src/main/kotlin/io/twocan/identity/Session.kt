package io.twocan.identity

import java.util.*

data class Session(
    val id: UUID,
    val user: User,
)
