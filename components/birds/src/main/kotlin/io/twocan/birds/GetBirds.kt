package io.twocan.birds

import io.twocan.http.GetResponse
import io.twocan.identity.SessionLens
import io.twocan.serialization.Json.auto
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind

internal object GetBirds {
    private val getBirdsResponseBodyLens = Body.auto<GetResponse<List<Bird>>>().toLens()
    operator fun invoke(sessionLens: SessionLens): RoutingHttpHandler {
        return "/api/birds" bind Method.GET to { request ->
            val birds = when (val session = sessionLens(request)) {
                null -> emptyList()
                else -> birdsByUserIdRepository.getOrDefault(session.user.id, emptyMap()).values.toList()
            }
            getBirdsResponseBodyLens.inject(
                GetResponse.Ok(birds),
                Response(Status.OK)
            )
        }
    }
}
