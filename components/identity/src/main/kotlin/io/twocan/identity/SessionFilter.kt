package io.twocan.identity

import mu.KotlinLogging
import org.http4k.core.Filter
import org.http4k.core.cookie.invalidateCookie
import org.http4k.core.with
import org.http4k.lens.Cookies
import java.util.*

internal object SessionFilter {
    private val logger = KotlinLogging.logger {}
    private val sessionCookieLens = Cookies.optional("session")
    operator fun invoke(sessionLens: SessionLens): Filter = Filter { next ->
        { request ->
            when (val cookie = sessionCookieLens(request)) {
                null -> next(request)
                else -> {
                    val sessionId = cookie.let {
                        try {
                            UUID.fromString(cookie.value)
                        } catch (e: IllegalArgumentException) {
                            logger.error { "Session retrieval failed because the session cookie was invalid - \"${cookie.value}\"" }
                            null
                        }
                    }
                    when (sessionId) {
                        null -> {
                            next(request).invalidateCookie("session")
                        }
                        else -> {
                            when (val userId = userIdBySessionIdRepository[sessionId]) {
                                null -> {
                                    logger.error { "Session retrieval failed because the session did not exist - \"${cookie.value}\"" }
                                    next(request).invalidateCookie("session")
                                }
                                else -> {
                                    when (val user = userByUserIdRepository[userId]) {
                                        null -> {
                                            logger.error { "Session retrieval failed because the user associated with the session did not exist - \"${cookie.value}\"" }
                                            userIdBySessionIdRepository.remove(sessionId)
                                            next(request).invalidateCookie("session")
                                        }
                                        else -> {
                                            val session = Session(id = sessionId, user = user)
                                            logger.trace { "Session retrieval succeeded - \"${user.emailAddress}\"" }
                                            next(request.with(sessionLens of session))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
