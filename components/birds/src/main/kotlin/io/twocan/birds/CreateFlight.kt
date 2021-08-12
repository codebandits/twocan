package io.twocan.birds

import io.konform.validation.Invalid
import io.konform.validation.Valid
import io.konform.validation.Validation
import io.konform.validation.jsonschema.minLength
import io.twocan.http.SubmitResponse
import io.twocan.identity.SessionLens
import io.twocan.serialization.Json.auto
import io.twocan.validation.Transformation
import io.twocan.validation.toBadRequestErrors
import io.twocan.validation.trim
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.lens.Path
import org.http4k.lens.uuid
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import java.time.Instant
import java.util.*

internal object CreateFlight {
    private val requestBodyLens = Body.auto<RequestBody>().toLens()
    private val submitResponseLens = Body.auto<SubmitResponse>().toLens()

    operator fun invoke(sessionLens: SessionLens): RoutingHttpHandler {
        return "/api/flights" bind Method.POST to { request ->
            val requestBody = requestBodyLens(request)




//            val bird = Bird(
//                id = UUID.randomUUID(),
//                firstName = requestBody.firstName,
//                lastName = requestBody.lastName,
//                lastFlight = null,
//            )
            val session = sessionLens(request)
            if (session != null) {
                val userId = session.user.id
                val userBirds = birdsByUserIdRepository.getOrDefault(userId, emptyMap()).toMutableMap()
                val bird = userBirds[requestBody.bird]
                if (bird != null) {
                    userBirds[requestBody.bird] = bird.copy(lastFlight = Instant.now())
                    birdsByUserIdRepository[userId] = userBirds
                }
            }
            submitResponseLens.inject(
                SubmitResponse.Created(UUID.randomUUID()),
                Response(Status.CREATED)
            )
        }
    }

    data class RequestBody(
        val bird: UUID,
    )
}
