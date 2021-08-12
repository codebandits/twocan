package io.twocan.validation

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.has
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

internal object TransformationTest : Spek({
    describe("Transformation") {
        describe("simple values") {
            it("should pass through data when there are no transformations") {
                val transformer = Transformation<String> { }
                val name = "Turtle"
                assertThat(transformer(name), equalTo(name))
            }

            it("should trim strings") {
                val transformer = Transformation<String> { trim() }
                val name = " Turtle  "
                assertThat(transformer(name), equalTo("Turtle"))
            }
        }

        describe("object properties") {

            data class Animal(val firstName: String)

            it("should pass through data when there are no transformations") {
                val transformer = Transformation<Animal> { }
                val animal = Animal(firstName = "Turtle")
                assertThat(transformer(animal), equalTo(animal))
            }

            it("should trim strings") {
                val transformer = Transformation<Animal> {
                    Animal::firstName { trim() }
                }
                val animal = Animal(firstName = " Turtle  ")
                assertThat(transformer(animal), has(Animal::firstName, equalTo("Turtle")))
            }
        }
    }
})
