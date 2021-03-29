package io.twocan.birds

import io.twocan.identity.client.SessionFilter
import io.twocan.identity.SessionLens
import org.http4k.cloudnative.env.Environment
import org.http4k.core.RequestContexts
import org.http4k.core.then
import org.http4k.filter.ServerFilters
import org.http4k.lens.RequestContextKey
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.routes

object BirdsRoutes {
    operator fun invoke(environment: Environment): RoutingHttpHandler {
        val contexts = RequestContexts()
        val sessionLens: SessionLens = RequestContextKey.optional(store = contexts, name = "session")
        val sessionFilter = SessionFilter(sessionLens = sessionLens, environment = environment)
        return ServerFilters.InitialiseRequestContext(contexts)
                .then(sessionFilter)
                .then(routes(
                        GetBirds(sessionLens = sessionLens),
                        CreateBird(sessionLens = sessionLens),
                        GetBird(sessionLens = sessionLens),
                ))
    }
}
