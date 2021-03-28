package io.twocan.identity

import org.http4k.core.RequestContexts
import org.http4k.core.then
import org.http4k.filter.ServerFilters
import org.http4k.lens.RequestContextKey
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.routes

object IdentityRoutes {
    operator fun invoke(): RoutingHttpHandler {
        val contexts = RequestContexts()
        val sessionLens: SessionLens = RequestContextKey.optional(store = contexts, name = "session")
        val sessionFilter = SessionFilter(sessionLens = sessionLens)
        return ServerFilters.InitialiseRequestContext(contexts)
                .then(sessionFilter)
                .then(routes(
                        GetSession(sessionLens = sessionLens),
                        Login(),
                        Logout(sessionLens = sessionLens),
                ))
    }
}
