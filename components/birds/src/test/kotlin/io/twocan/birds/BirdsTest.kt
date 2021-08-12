package io.twocan.birds

import com.natpryce.hamkrest.*
import com.natpryce.hamkrest.assertion.assertThat
import io.twocan.http.GetResponse
import io.twocan.http.SubmitResponse
import io.twocan.http.setupTestServer
import io.twocan.identity.Session
import io.twocan.identity.User
import io.twocan.serialization.Json.auto
import io.twocan.test.assertIsA
import org.http4k.cloudnative.env.Environment
import org.http4k.core.*
import org.http4k.hamkrest.hasBody
import org.http4k.hamkrest.hasStatus
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.util.*

internal object BirdsTest : Spek({
    describe("birds") {
        val getSessionResponseLens = Body.auto<GetResponse<Session?>>().toLens()
        var activeSession: Session? = null
        val mockIdentityService = routes("/api/session" bind Method.GET to {
            getSessionResponseLens.inject(GetResponse.Ok(activeSession), Response(Status.OK))
        })
        val identityUriString = setupTestServer(mockIdentityService)
        val environment = Environment.from("identity.uri" to identityUriString)
        val subject = BirdsRoutes(environment = environment)
        val createBirdRequestBodyLens = Body.auto<CreateBird.RequestBody>().toLens()
        val submitResponseLens = Body.auto<SubmitResponse>().toLens()
        val getBirdResponseLens = Body.auto<GetResponse<Bird>>().toLens()
        val getBirdsResponseLens = Body.auto<GetResponse<List<Bird>>>().toLens()

        beforeEachTest {
            activeSession = null
        }

        describe("when a bird has been created") {
            lateinit var createBirdResponse: Response
            val session = Session(id = UUID.randomUUID(), user = User(id = UUID.randomUUID(), emailAddress = "test@example.com"))
            beforeEachTest {
                activeSession = session
                val requestBody = CreateBird.RequestBody(
                    firstName = "Mark",
                    lastName = "Twain",
                )
                val request = createBirdRequestBodyLens(requestBody, Request(Method.POST, "/api/birds"))
                createBirdResponse = subject(request)
            }

            it("should return status Created") {
                assertThat(createBirdResponse, hasStatus(Status.CREATED))
            }

            it("should be able to be retrieved by the user who created it") {
                activeSession = session
                val createBirdResponseBody = submitResponseLens(createBirdResponse)
                assertThat(createBirdResponseBody, isA<SubmitResponse.Created>())
                val birdId = assertIsA<SubmitResponse.Created>(createBirdResponseBody).id
                val getBirdRequest = Request(Method.GET, "/api/birds/${birdId}")
                assertThat(
                    subject(getBirdRequest),
                    hasBody(getBirdResponseLens, isA(has(GetResponse.Ok<Bird>::data, has(Bird::id, equalTo(birdId)) and has(Bird::firstName, equalTo("Mark")) and has(Bird::lastName, equalTo("Twain")))))
                )
            }

            it("should be included in the list of birds for the user who created") {
                activeSession = session
                val createBirdResponseBody = submitResponseLens(createBirdResponse)
                val birdId = assertIsA<SubmitResponse.Created>(createBirdResponseBody).id
                val getBirdsRequest = Request(Method.GET, "/api/birds")
                assertThat(
                    subject(getBirdsRequest),
                    hasBody(getBirdsResponseLens, isA(has(GetResponse.Ok<List<Bird>>::data, anyElement(has(Bird::id, equalTo(birdId)) and has(Bird::firstName, equalTo("Mark")) and has(Bird::lastName, equalTo("Twain"))))))
                )
            }

            it("should not be able to be retrieved by anonymous users") {
                activeSession = null
                val createBirdResponseBody = submitResponseLens(createBirdResponse)
                val birdId = assertIsA<SubmitResponse.Created>(createBirdResponseBody).id
                val getBirdsRequest = Request(Method.GET, "/api/birds/${birdId}")
                assertThat(subject(getBirdsRequest), hasBody(getBirdResponseLens, isA<GetResponse.NotFound<Bird>>()))
            }

            it("should not be included in the list of birds for anonymous users") {
                activeSession = null
                submitResponseLens(createBirdResponse)
                val getBirdsRequest = Request(Method.GET, "/api/birds")
                assertThat(subject(getBirdsRequest), hasBody(getBirdsResponseLens, isA(has(GetResponse.Ok<List<Bird>>::data, isEmpty))))
            }
        }
    }
})
