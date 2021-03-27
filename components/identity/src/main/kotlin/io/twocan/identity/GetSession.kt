package io.twocan.identity


import io.twocan.http.ApiResponse
import io.twocan.http.toApiResponse
import mu.KotlinLogging
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import java.util.*

private val logger = KotlinLogging.logger {}

class GetSession {
    private val responseLens = Body.auto<ApiResponse<Session>>().toLens()
    operator fun invoke(): RoutingHttpHandler {
        return "/api/session" bind Method.GET to { request ->
            val session = Session(userId = UUID.randomUUID(), name = "Twocan Sam")
            logger.info { "found session for user id ${session.userId}" }
            responseLens.inject(session.toApiResponse(), Response(Status.OK))
        }
    }
}
