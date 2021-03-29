package io.twocan.birds

import java.util.*

internal val birdsByUserIdRepository = mutableMapOf<UUID, Map<UUID, Bird>>()
