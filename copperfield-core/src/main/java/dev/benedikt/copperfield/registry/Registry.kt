package dev.benedikt.copperfield.registry

import dev.benedikt.copperfield.converter.Converter

/**
 * The registry defined the default [Converter]s used by the [dev.benedikt.copperfield.CopperfieldAgent] it is passed to.
 *
 * Each [Converter] can optionally be assigned to a given `context`. When providing a [Converter], the converters of the given `context` will be
 * preferred. If there is no matching [Converter], the registry will fall back to the default converters without a `context` defined.
 *
 * @see dev.benedikt.copperfield.CopperfieldAgent
 * @see Converter
 *
 * @author Benedikt Wüller
 */
abstract class Registry {

    /**
     * Contains all converter types without a context assigned.
     */
    private val defaultConverterTypes = mutableMapOf<Class<*>, Class<out Converter<*, *>>>()

    /**
     * Contains converter types by context types.
     */
    private val converterTypes = mutableMapOf<Class<*>, MutableMap<Class<*>, Class<out Converter<*, *>>>>()

    /**
     * Contains converter instances by converter types.
     */
    private val converters = mutableMapOf<Class<out Converter<*, *>>, Converter<*, *>>()

    /**
     * Appends the given [converterType] for [ourType] and the given [contextType].
     * If no [contextType] is given, this converter will be used if no context specific converter can be found.
     * If a converter already exists for [ourType] in the given [contextType], the [converterType] will override the previous one.
     */
    @JvmOverloads
    open fun with(ourType: Class<*>, converterType: Class<out Converter<*, *>>, contextType: Class<*>? = null): Registry {
        if (contextType == null) {
            this.defaultConverterTypes[ourType] = converterType
        } else {
            this.converterTypes.getOrPut(contextType) { mutableMapOf() }[ourType] = converterType
        }
        return this
    }

    /**
     * Appends the given [converter] for [ourType] and the given [contextType].
     * If no [contextType] is given, this converter will be used if no context specific converter can be found.
     * If a converter already exists for [ourType] in the given [contextType], the [converter] will override the previous one.
     */
    @JvmOverloads
    open fun with(ourType: Class<*>, converter: Converter<out Any, out Any>, contextType: Class<*>? = null): Registry {
        converters[converter.javaClass] = converter
        if (contextType == null) {
            this.defaultConverterTypes[ourType] = converter.javaClass
        } else {
            this.converterTypes.getOrPut(contextType) { mutableMapOf() }[ourType] = converter.javaClass
        }
        return this
    }

    /**
     * Appends all default and context-aware converters of the [other] registry to this one.
     * This will also override provided converter instances.
     */
    open fun with(other: Registry): Registry {
        this.defaultConverterTypes.putAll(other.defaultConverterTypes)
        other.converterTypes.forEach { (ourType, converterTypes) -> this.converterTypes.getOrPut(ourType) { mutableMapOf() }.putAll(converterTypes) }
        this.converters.putAll(other.converters)
        return this
    }

    /**
     * Removes the [Converter] assigned to [ourType] for the given [contextType]
     * If no [contextType] is defined, converters without context will be removed.
     */
    @JvmOverloads
    open fun without(ourType: Class<*>, contextType: Class<*>? = null): Registry {
        if (contextType == null) {
            this.defaultConverterTypes.remove(ourType)
        } else {
            this.converterTypes[contextType]?.remove(ourType)
        }
        return this
    }

    /**
     * Removes all [Converter]s assigned to the [other] registry from this one.
     * Converters and converter instances will only be removed if both the key and value are the same.
     */
    open fun without(other: Registry): Registry {
        other.defaultConverterTypes.filter { this.defaultConverterTypes[it.key] == it.value }.forEach { this.defaultConverterTypes.remove(it.key) }
        other.converterTypes.forEach { (format, types) ->
            val ourTypes = this.converterTypes[format] ?: return@forEach
            types.filter { ourTypes[it.key] == it.value }.forEach { ourTypes.remove(it.key) }
        }
        other.converters.filter { this.converters[it.key]?.javaClass == it.value.javaClass }.forEach { this.converters.remove(it.key) }
        return this
    }

    /**
     * Returns the best converter matching the given [ourType] and [contextType].
     * If a [contextType] is given, [Converter]s of the same context will be checked in addition to the default converters without context.
     * The `best` converter is determined by assignability from [ourType] (so the closest to the given class). If there are multiple converters
     * on the same level, the order is not defined.
     */
    protected open fun getConverterType(ourType: Class<*>, contextType: Class<*>? = null): Class<out Converter<out Any, out Any>>? {
        val converters = mutableMapOf<Class<*>, Class<out Converter<*, *>>>()
        converters.putAll(this.defaultConverterTypes)

        if (contextType != null) {
            this.converterTypes.keys
                .filter { it.isAssignableFrom(contextType) }
                .forEach {
                    val converterTypes = this.converterTypes[it] ?: return@forEach
                    converters.putAll(converterTypes)
                }
        }

        // Sort by assignability to target format.
        val sorted = converters.entries
            .filter { it.key.isAssignableFrom(ourType) }
            .sortedWith(Comparator { o1, o2 ->
                if (o1.key.isAssignableFrom(o2.key)) {
                    return@Comparator -1
                } else if (o2.key.isAssignableFrom(o1.key)) {
                    return@Comparator 1
                }
                return@Comparator 0
            })

        return sorted.firstOrNull()?.value
    }

    /**
     * Returns the best converter matching [ourType] and the given [contextType].
     * @see [getConverterType]
     */
    @Suppress("UNCHECKED_CAST")
    open fun <O : Any, T : Any> getConverterByValueType(ourType: Class<*>, contextType: Class<*>? = null): Converter<out O, out T>? {
        val converterType = this.getConverterType(ourType, contextType) ?: return null
        return this.getConverterByType(converterType as Class<out Converter<out O, out T>>)
    }

    /**
     * Returns an instance assignable to the given [Converter] [type].
     */
    @Suppress("UNCHECKED_CAST")
    open fun <O : Any, T : Any> getConverterByType(type: Class<out Converter<out O, out T>>): Converter<out O, out T> {
        return converters.getOrPut(type) { type.newInstance() } as Converter<out O, out T>
    }

}
