package io.twocan.birds

import io.twocan.http.ApiResponse
import io.twocan.identity.SessionLens
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.lens.Path
import org.http4k.lens.uuid
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind

internal object GetBird {
    private val responseLens = Body.auto<ApiResponse<Bird?>>().toLens()
    operator fun invoke(sessionLens: SessionLens): RoutingHttpHandler {
        val birdIdLens = Path.uuid().of("birdId")
        return "/api/birds/{birdId}" bind Method.GET to { request ->
            val session = sessionLens(request)
            val bird = if (session != null) {
                val userId = session.id
                val birdId = birdIdLens(request)
                birdsByUserIdRepository.getOrDefault(userId, emptyMap())[birdId]
            } else {
                null
            }
            responseLens.inject(ApiResponse(bird), Response(Status.OK))
        }
    }
}
