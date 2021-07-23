package io.twocan.birds

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.has
import com.natpryce.hamkrest.hasElement
import com.natpryce.hamkrest.isA
import io.twocan.http.GetResponse
import io.twocan.http.SubmitResponse
import io.twocan.http.setupTestServer
import io.twocan.identity.Session
import io.twocan.identity.User
import io.twocan.serialization.Json.auto
import io.twocan.test.hasKeyValue
import org.http4k.cloudnative.env.Environment
import org.http4k.core.*
import org.http4k.hamkrest.hasBody
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.util.*

internal object BirdsValidationTest : Spek({
    describe("birds validation") {
        val getSessionResponseLens = Body.auto<GetResponse<Session?>>().toLens()
        val session = Session(id = UUID.randomUUID(), user = User(id = UUID.randomUUID(), emailAddress = "test@example.com"))
        val mockIdentityService = routes("/api/session" bind Method.GET to {
            getSessionResponseLens.inject(GetResponse.Ok(session), Response(Status.OK))
        })
        val identityUriString = setupTestServer(mockIdentityService)
        val environment = Environment.from("identity.uri" to identityUriString)
        val subject = BirdsRoutes(environment = environment)
        val createBirdRequestBodyLens = Body.auto<CreateBird.RequestBody>().toLens()
        val submitResponseLens = Body.auto<SubmitResponse>().toLens()

        val validRequestBody = CreateBird.RequestBody(
            firstName = "Mark",
            lastName = "Twain",
        )

        describe("when the request is valid") {
            val request = createBirdRequestBodyLens(validRequestBody, Request(Method.POST, "/api/birds"))

            it("should create the bird") {
                assertThat(subject(request), hasBody(submitResponseLens, isA<SubmitResponse.Created>()))
            }
        }

        describe("when the first name is blank") {
            val invalidRequestBody = validRequestBody.copy(firstName = "")
            val request = createBirdRequestBodyLens(invalidRequestBody, Request(Method.POST, "/api/birds"))

            it("should return errors") {
                assertThat(
                    subject(request), hasBody(
                        submitResponseLens, isA(
                            has(SubmitResponse.BadRequestErrors::errors, hasKeyValue("firstName", "required"))
                        )
                    )
                )
            }
        }
    }
})
