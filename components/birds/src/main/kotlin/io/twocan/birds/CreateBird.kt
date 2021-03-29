package io.twocan.birds

import io.twocan.http.ApiResponse
import io.twocan.identity.SessionLens
import kotlinx.serialization.Serializable
import mu.KotlinLogging
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import java.util.*

internal object CreateBird {
    private val logger = KotlinLogging.logger {}
    private val requestBodyLens = Body.auto<RequestBody>().toLens()
    private val responseLens = Body.auto<ApiResponse<UUID>>().toLens()
    operator fun invoke(sessionLens: SessionLens): RoutingHttpHandler {
        return "/api/birds" bind Method.POST to { request ->
            val requestBody = requestBodyLens(request)
            val bird = Bird(
                    id = UUID.randomUUID(),
                    firstName = requestBody.firstName,
                    lastName = requestBody.lastName
            )
            val session = sessionLens(request)
            if (session != null) {
                val userId = session.id
                val userBirds = birdsByUserIdRepository.getOrDefault(userId, emptyMap()).plus(bird.id to bird)
                birdsByUserIdRepository[userId] = userBirds
            }
            responseLens.inject(ApiResponse(bird.id), Response(Status.OK))
        }
    }

    @Serializable
    data class RequestBody(
            val firstName: String,
            val lastName: String,
    )
}
