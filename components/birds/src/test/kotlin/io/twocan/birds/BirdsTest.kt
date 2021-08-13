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
        val createFlightRequestBodyLens = Body.auto<CreateFlight.RequestBody>().toLens()
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


        describe("when a user has multiple birds") {
            lateinit var birdId1: UUID
            lateinit var birdId2: UUID
            lateinit var birdId3: UUID

            beforeEachTest {
                val session = Session(id = UUID.randomUUID(), user = User(id = UUID.randomUUID(), emailAddress = "test@example.com"))
                activeSession = session

                val requestBody1 = CreateBird.RequestBody(
                    firstName = "Adam",
                    lastName = "Bird",
                )
                val request1 = createBirdRequestBodyLens(requestBody1, Request(Method.POST, "/api/birds"))
                val createBirdResponse1 = subject(request1)
                val createBirdResponseBody1 = submitResponseLens(createBirdResponse1)
                birdId1 = assertIsA<SubmitResponse.Created>(createBirdResponseBody1).id

                val requestBody2 = CreateBird.RequestBody(
                    firstName = "Cody",
                    lastName = "Bird",
                )
                val request2 = createBirdRequestBodyLens(requestBody2, Request(Method.POST, "/api/birds"))
                val createBirdResponse2 = subject(request2)
                val createBirdResponseBody2 = submitResponseLens(createBirdResponse2)
                birdId2 = assertIsA<SubmitResponse.Created>(createBirdResponseBody2).id

                val requestBody3 = CreateBird.RequestBody(
                    firstName = "Brody",
                    lastName = "Bird",
                )
                val request3 = createBirdRequestBodyLens(requestBody3, Request(Method.POST, "/api/birds"))
                val createBirdResponse3 = subject(request3)
                val createBirdResponseBody3 = submitResponseLens(createBirdResponse3)
                birdId3 = assertIsA<SubmitResponse.Created>(createBirdResponseBody3).id
            }

            it("should sort them by name") {
                val getBirdsRequest = Request(Method.GET, "/api/birds")

                val birds = assertIsA<GetResponse.Ok<List<Bird>>>(getBirdsResponseLens(subject(getBirdsRequest))).data
                assertThat(birds[0], has(Bird::firstName, equalTo("Adam")))
                assertThat(birds[1], has(Bird::firstName, equalTo("Brody")))
                assertThat(birds[2], has(Bird::firstName, equalTo("Cody")))
            }

            describe("when some of the birds have flights") {
                beforeEachTest {
                    listOf(birdId3, birdId1).forEach { birdId ->
                        val requestBody = CreateFlight.RequestBody(birdId = birdId)
                        val request = createFlightRequestBodyLens(requestBody, Request(Method.POST, "/api/flights"))
                        val createFlightResponse = subject(request)
                        val createFlightResponseBody = submitResponseLens(createFlightResponse)
                        assertThat(createFlightResponseBody, isA<SubmitResponse.Created>())
                    }
                }

                it("should sort them by last flight and name") {
                    val getBirdsRequest = Request(Method.GET, "/api/birds")

                    val birds = assertIsA<GetResponse.Ok<List<Bird>>>(getBirdsResponseLens(subject(getBirdsRequest))).data
                    assertThat(birds[0], has(Bird::firstName, equalTo("Cody")))
                    assertThat(birds[1], has(Bird::firstName, equalTo("Brody")))
                    assertThat(birds[2], has(Bird::firstName, equalTo("Adam")))
                }
            }
        }
    }
})
