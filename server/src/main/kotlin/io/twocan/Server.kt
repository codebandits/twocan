package io.twocan

import io.twocan.identity.IdentityRoutes
import org.http4k.cloudnative.asK8sServer
import org.http4k.routing.routes
import org.http4k.server.Jetty

fun main() {
    val app = routes(
            IdentityRoutes(),
    )
    app.asK8sServer(::Jetty).start()
}
