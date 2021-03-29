package io.twocan.identity

import org.http4k.core.RequestContexts
import org.http4k.lens.RequestContextKey
import org.http4k.lens.RequestContextLens

typealias SessionLens = RequestContextLens<Session?>

object SessionRequestContextLens {
    operator fun invoke(contexts: RequestContexts): SessionLens =
            RequestContextKey.optional(store = contexts, name = "session")
}
