package dev.benedikt.copperfield

import dev.benedikt.copperfield.converter.Converter
import dev.benedikt.copperfield.exception.CopperFieldException
import dev.benedikt.copperfield.registry.BaseRegistry
import dev.benedikt.copperfield.registry.Registry
import java.lang.reflect.Field

/**
 * The Copperfield Agent (David) provides the ability to convert values of any type to values of any other type using predefined [Converter]s.
 *
 * The internal [registry] defines which value types should be converted to which target types and vice versa. The given [registries] are used as
 * templates to populate the internal [registry] at construction time. Changes to any of the given [registries] _do not_ affect the internal
 * [registry] after instantiation. Adjust the internal [registry] instead.
 *
 * The agent appends the given [registries] to a [BaseRegistry] instance, providing the default base converters. If this is not desired,
 * call [Registry.without] using the [BaseRegistry] after instantiation.
 *
 * @see Registry
 * @see Converter
 *
 * @author Benedikt Wüller
 */
open class CopperfieldAgent(private vararg val registries: Registry) {

    @Suppress("MemberVisibilityCanBePrivate")
    val registry: Registry = BaseRegistry()

    init {
        // Populate the internal registry using the given registry templates.
        registries.forEach(this.registry::with)
    }

    /**
     * Converts the [value] to the [Converter.theirType] of any converter with matching [Converter.ourType]. If there is no converter, the [value]
     * will be returned as is.
     *
     * If you want to convert the [value] for a given context, use [toTheirs] with the contextType. This may be desired if there are divergent
     * [Converter]s for specific contexts.
     */
    fun <O : Any> toTheirs(value: O?): Any {
        return this.toTheirs(value, Any::class.java)
    }

    fun <O : Any> toTheirsOrNull(value: O?): Any? {
        return this.toTheirsOrNull(value, Any::class.java)
    }

    /**
     * Converts the [value] from [O] to a type assignable from [T] if there is a converter defined in the [registry] matching those types withing the
     * given [contextType], falling back to converters using no context. If there is no converter, the [value] will be returned as is, if it is
     * assignable from [T]. Otherwise an [IllegalArgumentException] is thrown.
     *
     * If the [contextType] is of type [Any] or [Object], no context specific converters will be used. The [contextType] is passed to all underlying
     * converters recursively.
     */
    fun <O : Any, T : Any> toTheirs(value: O?, contextType: Class<out T>): T {
        return this.toTheirs(value, value?.javaClass ?: Any::class.java, contextType, null)!! as T
    }

    fun <O : Any, T : Any> toTheirsOrNull(value: O?, contextType: Class<out T>): T? {
        return this.toTheirs(value, value?.javaClass ?: Any::class.java, contextType, null) as T
    }

    /**
     * Converts the [value] from [O] to a type assignable from [T] if there is a converter defined in the [registry] matching those types withing the
     * given [contextType], falling back to converters using no context. If there is no converter, the [value] will be returned as is, if it is
     * assignable from [T]. Otherwise an [IllegalArgumentException] is thrown.
     *
     * If the [contextType] is of type [Any] or [Object], no context specific converters will be used. The [contextType] is passed to all underlying
     * converters recursively.
     *
     * The [field] should be defined if the conversion is happening in the context of a [Field].
     */
    @JvmOverloads
    fun <O : Any, T : Any> toTheirs(value: O?, ourType: Class<out O>, contextType: Class<out T>, field: Field? = null): Any? {
        val converter = this.findConverter<O, T>(ourType, contextType)

        // Try to recover, if there is no converter.
        if (converter == null) {
            if (value == null) return null
            return value as T
        }

        return this.toTheirsWithConverter(value, ourType, converter, contextType, field)
    }

    /**
     * Converts the [value] from [O] to a type assignable from [T] using a [Converter] of the given [converterType] and withing the given
     * [contextType].
     *
     * The [contextType] is passed to all underlying converters recursively.
     *
     * The [field] should be defined if the conversion is happening in the context of a [Field].
     */
    @JvmOverloads
    fun <O : Any, T : Any> toTheirsWithConverter(value: O?, ourType: Class<out O>, converterType: Class<out Converter<out O, out T>>, contextType: Class<out Any>, field: Field? = null): Any? {
        return this.toTheirsWithConverter(value, ourType, this.registry.getConverterByType(converterType), contextType, field)
    }

    /**
     * Converts the [value] from [O] to a type assignable from [T] using the given [converter] and withing the given [contextType].
     *
     * The [contextType] is passed to all underlying converters recursively.
     *
     * The [field] should be defined if the conversion is happening in the context of a [Field].
     */
    @JvmOverloads
    fun <O : Any, T : Any> toTheirsWithConverter(value: O?, ourType: Class<out O>, converter: Converter<out O, out T>, contextType: Class<out Any>, field: Field? = null): Any? {
        try {
            return (converter as Converter<O, T>).toTheirs(value, this, ourType, contextType, field)
        } catch (ex: Exception) {
            throw CopperFieldException(field, contextType, ex)
        }
    }

    /**
     * Converts the [value] of any type to a type assignable from [O] if there is a converter defined in the [registry] matching those types. If
     * there is no converter, the [value] will be returned as is if it is assignable from [O]. Otherwise an [IllegalArgumentException] is thrown.
     */
    fun <O : Any> toOurs(value: Any?, ourType: Class<out O>): O {
        return this.toOurs(value, ourType, value?.javaClass ?: Any::class.java, null)!!
    }

    fun <O : Any> toOursOrNull(value: Any?, ourType: Class<out O>): O? {
        return this.toOurs(value, ourType, value?.javaClass ?: Any::class.java, null)
    }

    /**
     * Converts the [value] from [T] to a type assignable from [O] if there is a converter defined in the [registry] matching those types withing the
     * given [contextType], falling back to converters using no context.
     *
     * If the [contextType] is of type [Any] or [Object], no context specific converters will be used. The [contextType] is passed to all underlying
     * converters recursively.
     *
     * The [field] should be defined if the conversion is happening in the context of a [Field].
     */
    @JvmOverloads
    fun <O : Any, T : Any> toOurs(value: T?, ourType: Class<out O>, contextType: Class<out T>, field: Field? = null): O? {
        val converter = this.findConverter<O, T>(ourType, contextType)

        if (converter == null) {
            if (value == null) return null
            return value as O
        }

        return this.toOursWithConverter(value, ourType, converter, contextType, field)
    }

    /**
     * Converts the [value] from [T] to a type assignable from [O] using a [Converter] of the given [converterType] and withing the given
     * [contextType].
     *
     * The [contextType] is passed to all underlying converters recursively.
     *
     * The [field] should be defined if the conversion is happening in the context of a [Field].
     */
    @JvmOverloads
    fun <O : Any, T : Any> toOursWithConverter(value: T?, ourType: Class<out O>, converterType: Class<out Converter<out O, out T>>, contextType: Class<out Any>, field: Field? = null): O? {
        return this.toOursWithConverter(value, ourType, this.registry.getConverterByType(converterType), contextType, field)
    }

    /**
     * Converts the [value] from [T] to a type assignable from [O] using the given [converter] and withing the given [contextType].
     *
     * The [contextType] is passed to all underlying converters recursively.
     *
     * The [field] should be defined if the conversion is happening in the context of a [Field].
     */
    @JvmOverloads
    fun <O : Any, T : Any> toOursWithConverter(value: T?, ourType: Class<out O>, converter: Converter<out O, out T>, contextType: Class<out Any>, field: Field? = null): O? {
        try {
            return (converter as Converter<O, T>).toOurs(value, this, ourType, contextType, field)
        } catch (ex: Exception) {
            throw CopperFieldException(field, contextType, ex)
        }
    }

    /**
     * Returns an instance of the given [converterType]. If the [converterType] is equal to the base [Converter] interface, an instance of the best
     * converter matching [type] and [contextType] will be returned. Returns `null` if there is no matching converter type.
     */
    fun findConverter(type: Class<*>, converterType: Class<out Converter<out Any, out Any>>, contextType: Class<*>): Converter<out Any, out Any>? {
        // If the given converter type is the base interface, this is an indicator for using the best converter.
        if (converterType == Converter::class.java) {
            return this.findConverter<Any, Any>(type, contextType)
        }

        return this.registry.getConverterByType(converterType)
    }

    /**
     * Finds and returns an instance of the best converter matching the given [type] and [contextType] or `null` if there is none.
     */
    private fun <O : Any, T : Any> findConverter(type: Class<out O>, contextType: Class<out Any>): Converter<out O, out T>? {
        return this.registry.getConverterByValueType(type, contextType)
    }

}
