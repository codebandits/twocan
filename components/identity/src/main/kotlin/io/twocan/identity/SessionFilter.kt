package io.twocan.identity

import mu.KotlinLogging
import org.http4k.core.Filter
import org.http4k.core.cookie.cookie
import org.http4k.core.cookie.invalidateCookie
import org.http4k.core.with
import java.util.*

internal object SessionFilter {
    private val logger = KotlinLogging.logger {}
    operator fun invoke(sessionLens: SessionLens): Filter = Filter { next ->
        { request ->
            when (val cookie = request.cookie("session")) {
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
                            when (val emailAddress = sessions[sessionId]) {
                                null -> {
                                    logger.error { "Session retrieval failed because the session did not exist - \"${cookie.value}\"" }
                                    next(request).invalidateCookie("session")
                                }
                                else -> {
                                    val session = Session(id = sessionId, emailAddress = emailAddress)
                                    logger.trace { "Session retrieval succeeded - \"${session.emailAddress}\"" }
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
