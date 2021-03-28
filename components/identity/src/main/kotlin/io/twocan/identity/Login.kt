package io.twocan.identity

import kotlinx.serialization.Serializable
import mu.KotlinLogging
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.cookie.Cookie
import org.http4k.core.cookie.cookie
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import java.util.*

internal object Login {
    private val logger = KotlinLogging.logger {}
    private val requestBodyLens = Body.auto<RequestBody>().toLens()
    operator fun invoke(): RoutingHttpHandler {
        return "/api/login" bind Method.POST to { request ->
            val requestBody = requestBodyLens(request)
            val sessionId = UUID.randomUUID()
            sessions[sessionId] = requestBody.emailAddress
            logger.info { "Login successful - \"${requestBody.emailAddress}\"" }
            Response(Status.OK).cookie(Cookie(name = "session", value = sessionId.toString()))
        }
    }

    @Serializable
    data class RequestBody(val emailAddress: String)
}
