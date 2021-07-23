package io.twocan.identity

import com.natpryce.hamkrest.anything
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.has
import com.natpryce.hamkrest.isA
import io.twocan.http.GetResponse
import io.twocan.http.SubmitResponse
import io.twocan.http.hasInvalidatedCookie
import io.twocan.http.hasNotSetCookie
import io.twocan.serialization.Json.auto
import io.twocan.test.assertIsA
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.core.cookie.cookie
import org.http4k.core.cookie.cookies
import org.http4k.hamkrest.hasBody
import org.http4k.hamkrest.hasCookieValue
import org.http4k.hamkrest.hasSetCookie
import org.http4k.hamkrest.hasStatus
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.util.*

object SessionTest : Spek({
    describe("authentication") {
        val loginRequestBodyLens = Body.auto<Login.RequestBody>().toLens()
        val submitResponseLens = Body.auto<SubmitResponse>().toLens()
        val getSessionResponseLens = Body.auto<GetResponse<Session?>>().toLens()
        val subject = IdentityRoutes()

        describe("when a user authenticates") {
            val emailAddress = "bird@example.com"
            val loginRequest = loginRequestBodyLens(
                Login.RequestBody(emailAddress = emailAddress),
                Request(Method.POST, "/api/login"),
            )
            val loginResponse = subject(loginRequest)

            it("should return status Accepted") {
                assertThat(loginResponse, hasStatus(Status.ACCEPTED))
            }

            it("should return body Accepted") {
                assertThat(loginResponse, hasBody(submitResponseLens, isA<SubmitResponse.Accepted>()))
            }

            it("should set the session cookie") {
                assertThat(loginResponse, hasSetCookie("session", hasCookieValue(anything)))
            }
        }

        describe("when a user authenticates multiple times") {
            val emailAddress = "bird@example.com"
            val loginRequest = loginRequestBodyLens(
                Login.RequestBody(emailAddress = emailAddress),
                Request(Method.POST, "/api/login"),
            )
            val loginResponse1 = subject(loginRequest)
            val sessionCookie1 = loginResponse1.cookies().single { it.name == "session" }.value
            val loginResponse2 = subject(loginRequest)
            val sessionCookie2 = loginResponse2.cookies().single { it.name == "session" }.value

            it("should return unique session cookies") {
                assertThat(sessionCookie1, !equalTo(sessionCookie2))
            }

            it("should return the same user for each session") {
                val response1 = subject(Request(Method.GET, "/api/session").cookie("session", sessionCookie1))
                val getResponse1 = assertIsA<GetResponse.Ok<Session?>>(getSessionResponseLens(response1))
                val session1 = assertIsA<Session>(getResponse1.data)

                val response2 = subject(Request(Method.GET, "/api/session").cookie("session", sessionCookie2))
                val getResponse2 = assertIsA<GetResponse.Ok<Session?>>(getSessionResponseLens(response2))
                val session2 = assertIsA<Session>(getResponse2.data)

                assertThat(session1.user, equalTo(session2.user))
            }
        }

        describe("when a user without a session cookie requests their session") {
            val sessionRequest = Request(Method.GET, "/api/session")
            val sessionResponse = subject(sessionRequest)

            it("should return status Ok") {
                assertThat(sessionResponse, hasStatus(Status.OK))
            }

            it("should return body Ok with a null session") {
                assertThat(sessionResponse, hasBody(getSessionResponseLens, isA(has(GetResponse.Ok<Session?>::data, equalTo(null)))))
            }

            it("should not set the session cookie") {
                assertThat(sessionResponse, hasNotSetCookie("session"))
            }
        }

        describe("when a user with an invalid session cookie requests their session") {
            val sessionCookie = UUID.randomUUID().toString()
            val sessionRequest = Request(Method.GET, "/api/session").cookie("session", sessionCookie)
            val sessionResponse = subject(sessionRequest)

            it("should return status Ok") {
                assertThat(sessionResponse, hasStatus(Status.OK))
            }

            it("should return body Ok with a null session") {
                assertThat(sessionResponse, hasBody(getSessionResponseLens, isA(has(GetResponse.Ok<Session?>::data, equalTo(null)))))
            }

            it("should remove the session cookie") {
                assertThat(sessionResponse, hasInvalidatedCookie("session"))
            }
        }

        describe("when a user with a valid session cookie requests their session") {
            val emailAddress = "bird@example.com"
            val loginRequest = loginRequestBodyLens(
                Login.RequestBody(emailAddress = emailAddress),
                Request(Method.POST, "/api/login"),
            )
            val loginResponse = subject(loginRequest)
            val sessionCookie = loginResponse.cookies().single { it.name == "session" }
            val sessionRequest = Request(Method.GET, "/api/session").cookie(sessionCookie)
            val sessionResponse = subject(sessionRequest)

            it("should return status Ok") {
                assertThat(sessionResponse, hasStatus(Status.OK))
            }

            it("should return body Ok with session containing the user's email address") {
                assertThat(sessionResponse, hasBody(getSessionResponseLens, isA(has(GetResponse.Ok<Session>::data, has(Session::user, (has(User::emailAddress, equalTo(emailAddress))))))))
            }

            it("should not set the session cookie") {
                assertThat(sessionResponse, hasNotSetCookie("session"))
            }
        }

        describe("when a user with a valid session cookie logs out") {
            val emailAddress = "bird@example.com"
            val loginRequest = loginRequestBodyLens(
                Login.RequestBody(emailAddress = emailAddress),
                Request(Method.POST, "/api/login"),
            )
            val loginResponse = subject(loginRequest)
            val sessionCookie = loginResponse.cookies().single { it.name == "session" }
            val logoutRequest = Request(Method.POST, "/api/logout").cookie(sessionCookie)
            val logoutResponse = subject(logoutRequest)

            it("should return status Accepted") {
                assertThat(logoutResponse, hasStatus(Status.ACCEPTED))
            }

            it("should return body Accepted") {
                assertThat(loginResponse, hasBody(submitResponseLens, isA<SubmitResponse.Accepted>()))
            }

            it("should remove the session cookie") {
                assertThat(logoutResponse, hasInvalidatedCookie("session"))
            }

            describe("when the invalid session is requested again") {
                val sessionRequest = Request(Method.GET, "/api/session").cookie(sessionCookie)
                val sessionResponse = subject(sessionRequest)

                it("should return status Ok") {
                    assertThat(sessionResponse, hasStatus(Status.OK))
                }

                it("should return body Ok with a null session") {
                    assertThat(sessionResponse, hasBody(getSessionResponseLens, isA(has(GetResponse.Ok<Session?>::data, equalTo(null)))))
                }

                it("should remove the session cookie") {
                    assertThat(sessionResponse, hasInvalidatedCookie("session"))
                }
            }
        }
    }
})
