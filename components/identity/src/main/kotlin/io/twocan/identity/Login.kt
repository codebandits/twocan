package io.twocan.identity

import io.twocan.http.SubmitResponse
import io.twocan.serialization.Json.auto
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
    operator fun invoke(): RoutingHttpHandler {
        return "/api/login" bind Method.POST to { request ->
            val requestBody = requestBodyLens(request)
            val sessionId = UUID.randomUUID()
            val user = when (val existingUser =
                userByUserIdRepository.values.find { user -> user.emailAddress == requestBody.emailAddress }) {
                null -> User(
                    id = UUID.randomUUID(),
                    emailAddress = requestBody.emailAddress
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

    data class RequestBody(val emailAddress: String)
}
