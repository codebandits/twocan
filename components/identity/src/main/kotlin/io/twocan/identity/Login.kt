package io.twocan.identity

import io.konform.validation.Invalid
import io.konform.validation.Valid
import io.konform.validation.Validation
import io.twocan.http.SubmitResponse
import io.twocan.serialization.Json.auto
import io.twocan.validation.Transformation
import io.twocan.validation.emailAddress
import io.twocan.validation.toBadRequestErrors
import io.twocan.validation.trim
import mu.KotlinLogging
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.cookie.Cookie
import org.http4k.core.cookie.cookie
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import java.util.*

internal object Login {
    private val logger = KotlinLogging.logger {}
    private val requestBodyLens = Body.auto<RequestBody>().toLens()
    private val submitResponseLens = Body.auto<SubmitResponse>().toLens()
    private val transformer = Transformation<RequestBody> {
        RequestBody::emailAddress { trim() }
    }
    private val validator = Validation<RequestBody> {
        RequestBody::emailAddress { emailAddress() }
    }

    operator fun invoke(): RoutingHttpHandler {
        return "/api/login" bind Method.POST to { request ->
            val requestBody = requestBodyLens(request)

            when (val validationResult = validator(transformer(requestBody))) {
                is Invalid -> {
                    submitResponseLens.inject(
                        validationResult.toBadRequestErrors(),
                        Response(Status.BAD_REQUEST)
                    )
                }
                is Valid -> {
                    val sessionId = UUID.randomUUID()
                    val user = when (val existingUser = userByUserIdRepository.values.find { user -> user.emailAddress == validationResult.value.emailAddress }) {
                        null -> User(
                            id = UUID.randomUUID(),
                            emailAddress = validationResult.value.emailAddress
                        ).also { userByUserIdRepository[it.id] = it }
                        else -> existingUser
                    }
                    userIdBySessionIdRepository[sessionId] = user.id
                    logger.info { "Login successful - \"${user.emailAddress}\"" }
                    submitResponseLens.inject(
                        SubmitResponse.Accepted(),
                        Response(Status.ACCEPTED).cookie(Cookie(name = "session", value = sessionId.toString()))
                    )
                }
            }
        }
    }

    data class RequestBody(val emailAddress: String)
}
