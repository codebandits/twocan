package io.twocan.identity

import io.twocan.http.SubmitResponse
import io.twocan.serialization.Json.auto
import mu.KotlinLogging
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.cookie.invalidateCookie
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind

internal object Logout {
    private val logger = KotlinLogging.logger {}
    private val submitResponseLens = Body.auto<SubmitResponse>().toLens()
    operator fun invoke(sessionLens: SessionLens): RoutingHttpHandler {
        return "/api/logout" bind Method.POST to { request ->
            when (val session = sessionLens(request)) {
                null -> {
                    Response(Status.OK)
                }
                else -> {
                    userIdBySessionIdRepository.remove(session.id)
                    logger.info { "Logout successful - \"${session.user.emailAddress}\"" }
                    submitResponseLens.inject(
                        SubmitResponse.Accepted(),
                        Response(Status.ACCEPTED).invalidateCookie("session")
                    )
                }
            }
        }
    }
}
