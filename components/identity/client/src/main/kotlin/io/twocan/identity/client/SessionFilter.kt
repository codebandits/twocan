package io.twocan.identity.client

import io.twocan.http.ApiResponse
import io.twocan.identity.Session
import io.twocan.identity.SessionLens
import org.http4k.client.ApacheClient
import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.core.*
import org.http4k.core.cookie.Cookie
import org.http4k.core.cookie.cookie
import org.http4k.core.cookie.cookies
import org.http4k.format.KotlinxSerialization.auto

object SessionFilter {
    private val client = ApacheClient()
    private val identityUriLens = EnvironmentKey.map(Uri.Companion::of).required("identity.uri")
    private val sessionResponseLens = Body.auto<ApiResponse<Session?>>().toLens()

    operator fun invoke(sessionLens: SessionLens, environment: Environment): Filter {
        val getSessionUri = identityUriLens(environment).path("/api/session")
        val getSessionRequest = Request(Method.GET, getSessionUri)
        return Filter { next ->
            { request ->
                val sessionRequest = when (val sessionCookie = request.cookie("session")) {
                    null -> getSessionRequest
                    else -> getSessionRequest.cookie(sessionCookie)
                }
                val sessionResponse = client(sessionRequest)
                val session = sessionResponseLens(sessionResponse).data

                next(request.with(sessionLens of session))
                        .setCookies(sessionResponse.cookies())
            }
        }
    }

    private fun Response.setCookies(cookies: List<Cookie>): Response =
            cookies.foldRight(this, { cookie, acc -> acc.cookie(cookie) })
}
