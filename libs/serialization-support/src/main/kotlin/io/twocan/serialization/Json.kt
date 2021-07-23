package io.twocan.serialization

import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.SingletonSupport
import org.http4k.format.ConfigurableJackson
import org.http4k.format.asConfigurable
import org.http4k.format.withStandardMappings

object Json : ConfigurableJackson(
    KotlinModule()
        .asConfigurable()
        .withStandardMappings()
        .done()
        .registerModule(KotlinModule(singletonSupport = SingletonSupport.CANONICALIZE))
)
