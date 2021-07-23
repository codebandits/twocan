package io.twocan.test

import com.natpryce.hamkrest.MatchResult
import com.natpryce.hamkrest.Matcher
import com.natpryce.hamkrest.describe

inline fun <reified T> assertIsA(actual: Any?): T {
    when (actual) {
        is T -> return actual
        else -> {
            throw AssertionError(
                "expected: a value of type ${T::class}\nbut was $actual"
            )
        }
    }
}
