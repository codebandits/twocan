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
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import java.util.*

internal object CreateBird {
    private val requestBodyLens = Body.auto<RequestBody>().toLens()
    private val submitResponseLens = Body.auto<SubmitResponse>().toLens()
    private val transformer = Transformation<RequestBody> {
        RequestBody::firstName { trim() }
        RequestBody::lastName { trim() }
    }
    private val validator = Validation<RequestBody> {
        RequestBody::firstName { minLength(1) hint "required" }
        RequestBody::lastName { minLength(1) hint "required" }
    }

    operator fun invoke(sessionLens: SessionLens): RoutingHttpHandler {
        return "/api/birds" bind Method.POST to { request ->
            val requestBody = requestBodyLens(request)
            when (val validationResult = validator(transformer(requestBody))) {
                is Invalid -> {
                    submitResponseLens.inject(
                        validationResult.toBadRequestErrors(),
                        Response(Status.BAD_REQUEST)
                    )
                }
                is Valid -> {
                    val bird = Bird(
                        id = UUID.randomUUID(),
                        firstName = validationResult.value.firstName,
                        lastName = validationResult.value.lastName
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
        }
    }

    data class RequestBody(
        val firstName: String,
        val lastName: String,
    )
}
