package io.twocan.validation

import com.natpryce.hamkrest.*
import com.natpryce.hamkrest.assertion.assertThat
import io.konform.validation.Invalid
import io.konform.validation.Valid
import io.konform.validation.Validation
import io.konform.validation.ValidationError
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

internal object ValidationTest : Spek({
    describe("Validation") {
        describe("isEmailAddress") {
            it("should return valid when the email address is valid") {
                val validator = Validation<String> { emailAddress() }
                val emailAddress = "zebra@example.com"
                assertThat(validator(emailAddress), isA<Valid<String>>())
            }

            it("should return an error when the email address is invalid") {
                val validator = Validation<String> { emailAddress() }
                val emailAddress = "zebra"
                assertThat(validator(emailAddress), isA(has(Invalid<String>::errors, anyElement(has(ValidationError::message, equalTo("must be a valid email address"))))))
            }
        }
    }
})
