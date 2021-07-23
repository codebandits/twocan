package io.twocan.birds

import io.twocan.http.SubmitResponse
import io.twocan.identity.SessionLens
import io.twocan.serialization.Json.auto
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import java.util.*

internal object CreateBird {
    private val requestBodyLens = Body.auto<RequestBody>().toLens()
    private val submitResponseLens = Body.auto<SubmitResponse>().toLens()
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
                val userId = session.user.id
                val userBirds = birdsByUserIdRepository.getOrDefault(userId, emptyMap()).plus(bird.id to bird)
                birdsByUserIdRepository[userId] = userBirds
            }
            submitResponseLens.inject(
                SubmitResponse.Created(bird.id),
                Response(Status.CREATED)
            )
        }
    }

    data class RequestBody(
        val firstName: String,
        val lastName: String,
    )
}
