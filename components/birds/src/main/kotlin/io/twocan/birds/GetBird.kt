package io.twocan.birds

import io.twocan.http.GetResponse
import io.twocan.identity.SessionLens
import io.twocan.serialization.Json.auto
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.Path
import org.http4k.lens.uuid
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind

internal object GetBird {
    private val getBirdResponseBodyLens = Body.auto<GetResponse<Bird>>().toLens()
    operator fun invoke(sessionLens: SessionLens): RoutingHttpHandler {
        val birdIdLens = Path.uuid().of("birdId")
        return "/api/birds/{birdId}" bind Method.GET to { request ->
            val session = sessionLens(request)
            val bird = if (session != null) {
                val userId = session.user.id
                val birdId = birdIdLens(request)
                birdsByUserIdRepository.getOrDefault(userId, emptyMap())[birdId]
            } else {
                null
            }
            when (bird) {
                null -> getBirdResponseBodyLens(GetResponse.NotFound("Bird not found."), Response(Status.NOT_FOUND))
                else -> getBirdResponseBodyLens(GetResponse.Ok(bird), Response(Status.OK))
            }
        }
    }
}
