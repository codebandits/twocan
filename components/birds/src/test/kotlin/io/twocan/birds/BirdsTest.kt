package io.twocan.birds

import com.natpryce.hamkrest.*
import com.natpryce.hamkrest.assertion.assertThat
import io.twocan.http.ApiResponse
import io.twocan.http.setupTestServer
import io.twocan.identity.Session
import io.twocan.identity.User
import org.http4k.cloudnative.env.Environment
import org.http4k.core.*
import org.http4k.format.KotlinxSerialization.auto
import org.http4k.hamkrest.hasBody
import org.http4k.hamkrest.hasStatus
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.util.*

internal object BirdsTest : Spek({
    describe("birds") {
        val sessionApiResponseLens = Body.auto<ApiResponse<Session?>>().toLens()
        var activeSession: Session? = null
        val mockIdentityService = routes("/api/session" bind Method.GET to {
            sessionApiResponseLens.inject(ApiResponse(activeSession), Response(Status.OK))
        })
        val identityUriString = setupTestServer(mockIdentityService)
        val environment = Environment.from("identity.uri" to identityUriString)
        val subject = BirdsRoutes(environment = environment)
        val createBirdRequestBodyLens = Body.auto<CreateBird.RequestBody>().toLens()
        val createBirdResponseBodyLens = Body.auto<ApiResponse<UUID>>().map { it.data }.toLens()
        val getBirdResponseBodyLens = Body.auto<ApiResponse<Bird?>>().toLens()
        val getBirdsResponseBodyLens = Body.auto<ApiResponse<List<Bird>>>().toLens()

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

            it("should return status OK") {
                assertThat(createBirdResponse, hasStatus(Status.OK))
            }

            it("should be able to be retrieved by the user who created it") {
                activeSession = session
                val birdId = createBirdResponseBodyLens(createBirdResponse)
                val getBirdRequest = Request(Method.GET, "/api/birds/${birdId}")
                val expectedResponse = ApiResponse(Bird(id = birdId, firstName = "Mark", lastName = "Twain"))
                assertThat(subject(getBirdRequest), hasBody(getBirdResponseBodyLens, equalTo(expectedResponse)))
            }

            it("should be included in the list of birds for the user who created") {
                activeSession = session
                val birdId = createBirdResponseBodyLens(createBirdResponse)
                val getBirdRequest = Request(Method.GET, "/api/birds")
                val expectedBird = Bird(id = birdId, firstName = "Mark", lastName = "Twain")
                val response = getBirdsResponseBodyLens(subject(getBirdRequest))
                assertThat(response.data, hasElement(expectedBird))
            }

            it("should not be able to be retrieved by anonymous users") {
                activeSession = null
                val birdId = createBirdResponseBodyLens(createBirdResponse)
                val getBirdRequest = Request(Method.GET, "/api/birds/${birdId}")
                val expectedResponse = ApiResponse(null)
                assertThat(subject(getBirdRequest), hasBody(getBirdResponseBodyLens, equalTo(expectedResponse)))
            }

            it("should be included in the list of birds for anonymous users") {
                activeSession = null
                createBirdResponseBodyLens(createBirdResponse)
                val getBirdRequest = Request(Method.GET, "/api/birds")
                val response = getBirdsResponseBodyLens(subject(getBirdRequest))
                assertThat(response.data, isEmpty)
            }
        }
    }
})
