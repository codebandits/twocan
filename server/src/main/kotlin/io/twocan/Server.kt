package io.twocan

import io.twocan.birds.BirdsRoutes
import io.twocan.identity.IdentityRoutes
import org.http4k.cloudnative.asK8sServer
import org.http4k.cloudnative.env.Environment
import org.http4k.routing.routes
import org.http4k.server.Jetty

fun main() {
    val environment = Environment.ENV
    val app = routes(
            BirdsRoutes(environment),
            IdentityRoutes(),
    )
    app.asK8sServer(::Jetty).start()
}
