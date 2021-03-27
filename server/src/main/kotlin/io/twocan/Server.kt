package io.twocan

import io.twocan.identity.GetSession
import org.http4k.cloudnative.asK8sServer
import org.http4k.server.Jetty

fun main() {
    val app = org.http4k.routing.routes(
            GetSession()()
    )
    app.asK8sServer(::Jetty).start()
}
