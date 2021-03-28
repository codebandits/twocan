package io.twocan.identity

import org.http4k.lens.RequestContextLens

typealias SessionLens = RequestContextLens<Session?>
