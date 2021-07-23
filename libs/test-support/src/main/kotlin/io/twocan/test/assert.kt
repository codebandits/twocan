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

fun <K, V> hasKeyValue(key: K, value: V): Matcher<Map<K, V>> = object : Matcher.Primitive<Map<K, V>>() {
    override fun invoke(actual: Map<K, V>): MatchResult {
        val actualValue = try {
            actual.getValue(key)
        } catch (e: NoSuchElementException) {
            return MatchResult.Mismatch("did not contain the key ${describe(key)}")
        }
        return if (actualValue == value) {
            MatchResult.Match
        } else {
            MatchResult.Mismatch("the key ${describe(key)} contained the value ${describe(actualValue)}")
        }
    }

    override val description: String get() = "contains the key ${describe(key)} with the value ${describe(value)}"
    override val negatedDescription: String get() = "does not contain the key ${describe(key)} with the value ${describe(value)}"
}
