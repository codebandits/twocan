package io.twocan.http

import org.http4k.routing.RoutingHttpHandler
import org.http4k.server.Jetty
import org.http4k.server.asServer
import java.net.ServerSocket
import org.spekframework.spek2.style.specification.Suite

fun Suite.setupTestServer(httpHandler: RoutingHttpHandler): String {
  val port = ServerSocket(0).localPort
  val server by memoized { httpHandler.asServer(Jetty(port = port)) }
  beforeEachTest { server.start() }
  afterEachTest { server.stop() }
  return "http://localhost:$port"
}
