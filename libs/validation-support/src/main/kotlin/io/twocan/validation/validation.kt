package io.twocan.validation

import io.konform.validation.Invalid
import io.konform.validation.ValidationErrors
import io.twocan.http.SubmitResponse

internal fun ValidationErrors.toResponseErrors(): Map<String, String> = fold(mutableMapOf()) { acc, validationError ->
    acc[validationError.dataPath.removePrefix(".")] = validationError.message
    acc
}

fun ValidationErrors.toBadRequestErrors() = SubmitResponse.BadRequestErrors(errors = toResponseErrors())

fun <T> Invalid<T>.toBadRequestErrors() = errors.toBadRequestErrors()
