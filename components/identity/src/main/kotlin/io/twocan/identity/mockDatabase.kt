package io.twocan.identity

import java.util.*

internal val userIdBySessionIdRepository = mutableMapOf<UUID, UUID>()
internal val userByUserIdRepository = mutableMapOf<UUID, User>()
