package io.twocan.identity

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.has
import com.natpryce.hamkrest.isA
import io.twocan.http.GetResponse
import io.twocan.http.SubmitResponse
import io.twocan.serialization.Json.auto
import io.twocan.test.hasKeyValue
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.cookie.cookie
import org.http4k.core.cookie.cookies
import org.http4k.hamkrest.hasBody
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object SessionValidationTest : Spek({
    describe("authentication") {
        val loginRequestBodyLens = Body.auto<Login.RequestBody>().toLens()
        val submitResponseLens = Body.auto<SubmitResponse>().toLens()
        val getSessionResponseLens = Body.auto<GetResponse<Session?>>().toLens()
        val subject = IdentityRoutes()

        describe("when a user authenticates with a valid email address") {
            val emailAddress = "bird@example.com"
            val loginRequest = loginRequestBodyLens(
                Login.RequestBody(emailAddress = emailAddress),
                Request(Method.POST, "/api/login"),
            )
            val loginResponse = subject(loginRequest)

            it("should return body Accepted") {
                assertThat(loginResponse, hasBody(submitResponseLens, isA<SubmitResponse.Accepted>()))
            }
        }

        describe("when a user authenticates with a valid email address containing leading/trailing whitespace") {
            val emailAddress = "bird@example.com"
            val loginRequest = loginRequestBodyLens(
                Login.RequestBody(emailAddress = """ $emailAddress """),
                Request(Method.POST, "/api/login"),
            )
            val loginResponse = subject(loginRequest)
            val sessionCookie = loginResponse.cookies().single { it.name == "session" }
            val sessionRequest = Request(Method.GET, "/api/session").cookie(sessionCookie)
            val sessionResponse = subject(sessionRequest)

            it("should trim the whitespace") {
                assertThat(sessionResponse, hasBody(getSessionResponseLens, isA(has(GetResponse.Ok<Session>::data, has(Session::user, (has(User::emailAddress, equalTo(emailAddress))))))))
            }
        }

        describe("when a user authenticates with an invalid email address") {
            val emailAddress = "bird"
            val loginRequest = loginRequestBodyLens(
                Login.RequestBody(emailAddress = emailAddress),
                Request(Method.POST, "/api/login"),
            )
            val loginResponse = subject(loginRequest)

            it("should return errors") {
                assertThat(loginResponse, hasBody(submitResponseLens, isA(has(SubmitResponse.BadRequestErrors::errors, hasKeyValue("emailAddress", "must be a valid email address")))))
            }
        }
    }
})
