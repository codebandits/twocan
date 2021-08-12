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
import io.twocan.test.isNowish
import org.http4k.cloudnative.env.Environment
import org.http4k.core.*
import org.http4k.hamkrest.hasBody
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.time.Instant
import java.util.*

internal object FlightsTest : Spek({
    describe("flights") {
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

        lateinit var birdId1: UUID
        lateinit var birdId2: UUID

        beforeEachTest {
            val session = Session(id = UUID.randomUUID(), user = User(id = UUID.randomUUID(), emailAddress = "test@example.com"))
            activeSession = session

            val requestBody1 = CreateBird.RequestBody(
                firstName = "Mark",
                lastName = "Twain",
            )
            val request1 = createBirdRequestBodyLens(requestBody1, Request(Method.POST, "/api/birds"))
            val createBirdResponse1 = subject(request1)
            val createBirdResponseBody1 = submitResponseLens(createBirdResponse1)
            birdId1 = assertIsA<SubmitResponse.Created>(createBirdResponseBody1).id

            val requestBody2 = CreateBird.RequestBody(
                firstName = "Huckleberry",
                lastName = "Finn",
            )
            val request2 = createBirdRequestBodyLens(requestBody2, Request(Method.POST, "/api/birds"))
            val createBirdResponse2 = subject(request2)
            val createBirdResponseBody2 = submitResponseLens(createBirdResponse2)
            birdId2 = assertIsA<SubmitResponse.Created>(createBirdResponseBody2).id
        }

        describe("before any flights have been created") {
            it("should show each bird's last flight time as null") {
                val getBirdsRequest = Request(Method.GET, "/api/birds")
                assertThat(subject(getBirdsRequest), hasBody(getBirdsResponseLens, isA(has(GetResponse.Ok<List<Bird>>::data, allElements(has(Bird::lastFlight, absent()))))))
            }
        }

        describe("when a flight has been created") {
            beforeEachTest {
                val requestBody = CreateFlight.RequestBody(bird = birdId1)
                val request = createFlightRequestBodyLens(requestBody, Request(Method.POST, "/api/flights"))
                val createFlightResponse = subject(request)
                val createFlightResponseBody = submitResponseLens(createFlightResponse)
                assertThat(createFlightResponseBody, isA<SubmitResponse.Created>())
            }

            it("should show that bird's last flight time as when the flight was created") {
                val getBirdRequest = Request(Method.GET, "/api/birds/${birdId1}")
                assertThat(subject(getBirdRequest), hasBody(getBirdResponseLens, isA(has(GetResponse.Ok<Bird>::data, has(Bird::lastFlight, present(isNowish()))))))
            }
        }
    }
})
