package io.twocan.validation

import kotlin.reflect.KProperty1
import kotlin.reflect.full.cast
import kotlin.reflect.full.instanceParameter
import kotlin.reflect.full.memberFunctions

interface Transformation<T : Any> {

    companion object {
        operator fun <T : Any> invoke(init: TransformationBuilder<T>.() -> Unit): Transformation<T> {
            val builder = TransformationBuilderImpl<T>()
            return builder.apply(init).build()
        }
    }

    fun transform(value: T): T
    operator fun invoke(value: T) = transform(value)
}

class Operation<R> internal constructor(val fn: (R) -> R)

abstract class TransformationBuilder<T : Any> {
    abstract fun build(): Transformation<T>
    abstract fun addOperation(fn: (T) -> T): Operation<T>
    abstract operator fun <R : Any> KProperty1<T, R>.invoke(init: TransformationBuilder<R>.() -> Unit)
}

internal class TransformationBuilderImpl<T : Any> : TransformationBuilder<T>() {

    private val operations = mutableListOf<Operation<T>>()

    override fun build(): Transformation<T> {
        return object : Transformation<T> {
            override fun transform(value: T): T {
                return operations.fold(value) { acc, operation -> operation.fn(acc) }
            }
        }
    }

    override fun <R : Any> KProperty1<T, R>.invoke(init: TransformationBuilder<R>.() -> Unit) {
        addOperation { value -> value.setByCopy(name, Transformation(init)(get(value))) }
    }

    override fun addOperation(fn: (T) -> T): Operation<T> {
        val operation = Operation(fn)
        operations.add(operation)
        return operation
    }

    private fun <V> T.setByCopy(propertyName: String, value: V): T {
        return with(this::class.memberFunctions.first { it.name == "copy" }) {
            callBy(mapOf(
                instanceParameter!! to this@setByCopy,
                parameters.first { it.name == propertyName } to value
            )).let { this@setByCopy::class.cast(it) }
        }
    }
}

fun TransformationBuilder<String>.trim(): Operation<String> {
    return addOperation { it.trim() }
}
