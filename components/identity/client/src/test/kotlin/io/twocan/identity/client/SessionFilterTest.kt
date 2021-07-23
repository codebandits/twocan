package io.twocan.identity.client

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.hasSize
import io.twocan.http.GetResponse
import io.twocan.http.hasInvalidatedCookie
import io.twocan.http.setupTestServer
import io.twocan.identity.Session
import io.twocan.identity.SessionRequestContextLens
import io.twocan.identity.User
import io.twocan.serialization.Json.auto
import org.http4k.cloudnative.env.Environment
import org.http4k.core.*
import org.http4k.core.cookie.cookie
import org.http4k.core.cookie.invalidateCookie
import org.http4k.filter.ServerFilters
import org.http4k.hamkrest.hasCookie
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.util.*

internal object SessionFilterTest : Spek({
    describe("SessionFilter") {
        val session = Session(
            id = UUID.randomUUID(),
            user = User(
                id = UUID.randomUUID(),
                emailAddress = "test@example.com",
            ),
        )
        val getSessionResponseBodyLens = Body.auto<GetResponse<Session?>>().toLens()

        var filtersCalled = false
        var requestToIdentityService: Request? = null
        var sessionInContext: Session? = null
        var response: Response? = null

        beforeEachTest {
            filtersCalled = false
            requestToIdentityService = null
            sessionInContext = null
            response = null
        }

        describe("when the session is valid") {
            val mockIdentityService = routes("/api/session" bind Method.GET to { request ->
                requestToIdentityService = request
                getSessionResponseBodyLens.inject(GetResponse.Ok(session), Response(Status.OK))
            })
            val identityUriString = setupTestServer(mockIdentityService)
            val environment = Environment.Companion.from("identity.uri" to identityUriString)
            val contexts = RequestContexts()
            val sessionLens = SessionRequestContextLens(contexts = contexts)
            val app = ServerFilters.InitialiseRequestContext(contexts)
                .then(SessionFilter(sessionLens = sessionLens, environment = environment))
                .then { request ->
                    filtersCalled = true
                    sessionInContext = sessionLens(request)
                    Response(Status.OK)
                }

            beforeEachTest {
                response = app(Request(Method.GET, "/").cookie("session", "session-cookie"))
            }

            it("should work") {
                getSessionResponseBodyLens.inject(GetResponse.Ok(session), Response(Status.OK))
            }

            it("should continue the filter chain") {
                assertThat(filtersCalled, equalTo(true))
            }

            it("should set the session in context") {
                assertThat(sessionInContext, equalTo(session))
            }

            it("should pass the session cookie to the identity service") {
                assertThat(requestToIdentityService!!, hasCookie("session", "session-cookie"))
            }

            it("should not add any headers to the response") {
                assertThat(response!!.headers, hasSize(equalTo(0)))
            }
        }

        describe("when the session is not set") {
            val mockIdentityService = routes("/api/session" bind Method.GET to { request ->
                requestToIdentityService = request
                getSessionResponseBodyLens.inject(GetResponse.Ok(null), Response(Status.OK))
            })
            val identityUriString = setupTestServer(mockIdentityService)
            val environment = Environment.Companion.from("identity.uri" to identityUriString)
            val contexts = RequestContexts()
            val sessionLens = SessionRequestContextLens(contexts = contexts)
            val app = ServerFilters.InitialiseRequestContext(contexts)
                .then(SessionFilter(sessionLens = sessionLens, environment = environment))
                .then { request ->
                    filtersCalled = true
                    sessionInContext = sessionLens(request)
                    Response(Status.OK)
                }

            beforeEachTest {
                response = app(Request(Method.GET, "/"))
            }

            it("should continue the filter chain") {
                assertThat(filtersCalled, equalTo(true))
            }

            it("should set the session in context to null") {
                assertThat(sessionInContext, equalTo(null))
            }

            it("should not pass a session cookie to the identity service") {
                assertThat(requestToIdentityService?.cookie("session"), equalTo(null))
            }

            it("should not add any headers to the response") {
                assertThat(response!!.headers, hasSize(equalTo(0)))
            }
        }

        describe("when the session is invalid") {
            val mockIdentityService = routes("/api/session" bind Method.GET to { request ->
                requestToIdentityService = request
                getSessionResponseBodyLens.inject(GetResponse.Ok(null), Response(Status.OK).invalidateCookie("session"))
            })
            val identityUriString = setupTestServer(mockIdentityService)
            val environment = Environment.Companion.from("identity.uri" to identityUriString)
            val contexts = RequestContexts()
            val sessionLens = SessionRequestContextLens(contexts = contexts)
            val app = ServerFilters.InitialiseRequestContext(contexts)
                .then(SessionFilter(sessionLens = sessionLens, environment = environment))
                .then { request ->
                    filtersCalled = true
                    sessionInContext = sessionLens(request)
                    Response(Status.OK)
                }

            beforeEachTest {
                response = app(Request(Method.GET, "/").cookie("session", "session-cookie"))
            }

            it("should continue the filter chain") {
                assertThat(filtersCalled, equalTo(true))
            }

            it("should set the session in context to null") {
                assertThat(sessionInContext, equalTo(null))
            }

            it("should forward the session cookie invalidation from the identity service") {
                assertThat(response!!, hasInvalidatedCookie("session"))
            }
        }
    }
})
