package io.twocan.identity

import mu.KotlinLogging
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.cookie.invalidateCookie
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind

internal object Logout {
    private val logger = KotlinLogging.logger {}
    operator fun invoke(sessionLens: SessionLens): RoutingHttpHandler {
        return "/api/logout" bind Method.POST to { request ->
            when (val session = sessionLens(request)) {
                null -> {
                    Response(Status.OK)
                }
                else -> {
                    userIdBySessionIdRepository.remove(session.id)
                    logger.info { "Logout successful - \"${session.user.emailAddress}\"" }
                    Response(Status.OK).invalidateCookie("session")
                }
            }
        }
    }
}
