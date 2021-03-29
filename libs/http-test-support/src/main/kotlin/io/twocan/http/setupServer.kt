package io.twocan.http

import org.http4k.core.HttpHandler
import org.http4k.core.Uri
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.spekframework.spek2.style.specification.Suite
import java.net.ServerSocket

fun Suite.setupTestServer(httpHandler: HttpHandler): String {
    val testSocket = ServerSocket(0)
    val port = testSocket.localPort
    testSocket.close()
    val server = httpHandler.asServer(Jetty(port = port))
    beforeGroup { server.start() }
    afterGroup { server.stop() }
    return Uri.of("http://localhost").port(port).toString()
}
