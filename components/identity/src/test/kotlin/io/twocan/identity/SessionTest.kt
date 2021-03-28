package io.twocan.identity

import com.natpryce.hamkrest.anything
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.twocan.http.ApiResponse
import io.twocan.http.hasInvalidatedCookie
import io.twocan.http.hasNotSetCookie
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status
import org.http4k.core.cookie.cookie
import org.http4k.core.cookie.cookies
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.hamkrest.hasBody
import org.http4k.hamkrest.hasCookieValue
import org.http4k.hamkrest.hasSetCookie
import org.http4k.hamkrest.hasStatus
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.util.*

object SessionTest : Spek({
    describe("authentication") {
        val sessionApiResponseLens = Body.auto<ApiResponse<Session?>>().toLens()
        val loginRequestBodyLens = Body.auto<Login.RequestBody>().toLens()
        val subject = IdentityRoutes()

        describe("when a user with a valid session cookie requests their session") {
            beforeEachTest {
                Request(Method.GET, "/api/login")
            }
        }

        describe("when a user authenticates") {
            val emailAddress = "bird@example.com"
            val request = loginRequestBodyLens(
                    Login.RequestBody(emailAddress = emailAddress),
                    Request(Method.POST, "/api/login"),
            )
            val response = subject(request)

            it("should return status OK") {
                assertThat(response, hasStatus(Status.OK))
            }

            it("should set the session cookie") {
                assertThat(response, hasSetCookie("session", hasCookieValue(anything)))
            }
        }

        describe("when a user without a session cookie requests their session") {
            val request = Request(Method.GET, "/api/session")
            val response = subject(request)

            it("should return status OK") {
                assertThat(response, hasStatus(Status.OK))
            }

            it("should return a null session") {
                assertThat(response, hasBody(sessionApiResponseLens, equalTo(ApiResponse(null))))
            }

            it("should not set the session cookie") {
                assertThat(response, hasNotSetCookie("session"))
            }
        }

        describe("when a user with an invalid session cookie requests their session") {
            val sessionCookie = UUID.randomUUID().toString()
            val request = Request(Method.GET, "/api/session").cookie("session", sessionCookie)
            val response = subject(request)

            it("should return status OK") {
                assertThat(response, hasStatus(Status.OK))
            }

            it("should return a null session") {
                assertThat(response, hasBody(sessionApiResponseLens, equalTo(ApiResponse(null))))
            }

            it("should remove the session cookie") {
                assertThat(response, hasInvalidatedCookie("session"))
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

            it("should return status OK") {
                assertThat(sessionResponse, hasStatus(Status.OK))
            }

            it("should return a session with the user's email address") {
                val expectedApiResponse = ApiResponse(Session(id = UUID.fromString(sessionCookie.value), emailAddress = emailAddress))
                assertThat(sessionResponse, hasBody(sessionApiResponseLens, equalTo(expectedApiResponse)))
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


            it("should return status OK") {
                assertThat(logoutResponse, hasStatus(Status.OK))
            }

            it("should remove the session cookie") {
                assertThat(logoutResponse, hasInvalidatedCookie("session"))
            }

            describe("when the invalid session is requested again") {
                val sessionRequest = Request(Method.GET, "/api/session").cookie(sessionCookie)
                val sessionResponse = subject(sessionRequest)

                it("should return status OK") {
                    assertThat(logoutResponse, hasStatus(Status.OK))
                }

                it("should return a null session") {
                    assertThat(sessionResponse, hasBody(sessionApiResponseLens, equalTo(ApiResponse(null))))
                }

                it("should remove the session cookie") {
                    assertThat(logoutResponse, hasInvalidatedCookie("session"))
                }
            }
        }
    }
})
