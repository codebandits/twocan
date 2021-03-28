package io.twocan.http

import com.natpryce.hamkrest.MatchResult
import com.natpryce.hamkrest.Matcher
import com.natpryce.hamkrest.describe
import com.natpryce.hamkrest.lessThan
import org.http4k.core.Response
import org.http4k.core.cookie.cookies
import org.http4k.hamkrest.hasCookieExpiry
import org.http4k.hamkrest.hasSetCookie
import java.time.LocalDateTime

fun hasInvalidatedCookie(name: String) = hasSetCookie(name, hasCookieExpiry(lessThan(LocalDateTime.now())))

fun hasNotSetCookie(cookieName: String): Matcher<Response> = object : Matcher.Primitive<Response>() {
    override val description: String = "a body without Cookie $cookieName"

    override fun invoke(actual: Response): MatchResult {
        return when (val cookie = actual.cookies().find { it.name == cookieName }) {
            null -> MatchResult.Match
            else -> MatchResult.Mismatch("had a body with Cookie that was: ${describe(cookie)}")
        }
    }
}
