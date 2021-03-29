package io.twocan.birds

import io.twocan.http.ApiResponse
import io.twocan.identity.SessionLens
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind

internal object GetBirds {
    private val responseLens = Body.auto<ApiResponse<List<Bird>>>().toLens()
    operator fun invoke(sessionLens: SessionLens): RoutingHttpHandler {
        return "/api/birds" bind Method.GET to { request ->
            val birds = when (val session = sessionLens(request)) {
                null -> emptyList()
                else -> birdsByUserIdRepository.getOrDefault(session.id, emptyMap()).values.toList()
            }
            responseLens.inject(ApiResponse(birds), Response(Status.OK))
        }
    }
}
