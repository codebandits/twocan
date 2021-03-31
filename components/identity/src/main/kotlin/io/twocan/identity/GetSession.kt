package io.twocan.identity


import io.twocan.http.ApiResponse
import mu.KotlinLogging
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind

internal object GetSession {
    private val logger = KotlinLogging.logger {}
    private val responseLens = Body.auto<ApiResponse<Session?>>().toLens()
    operator fun invoke(sessionLens: SessionLens): RoutingHttpHandler {
        return "/api/session" bind Method.GET to { request ->
            when (val session = sessionLens(request)) {
                null -> {
                    logger.trace { "user has no session" }
                    responseLens.inject(ApiResponse(null), Response(Status.OK))
                }
                else -> {
                    logger.trace { "found session for user id ${session.user.id}" }
                    responseLens.inject(ApiResponse(session), Response(Status.OK))
                }
            }
        }
    }
}

