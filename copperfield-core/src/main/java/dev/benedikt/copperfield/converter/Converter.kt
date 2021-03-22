package dev.benedikt.copperfield.converter

import dev.volix.rewinside.odyssey.common.copperfield.CopperfieldAgent
import java.lang.reflect.Field

/**
 * Provides functionality to convert objects of [O] to objects of [T] and vice versa.
 *
 * @author Benedikt Wüller
 */
abstract class Converter<O : Any, T : Any>(val ourType: Class<O>, val theirType: Class<T>) {

    /**
     * Transforms the [value] from [O] to [T].
     * The concrete type [T] to convert _from_ is defined in [ourType].
     * The [agent] is the [CopperfieldAgent] which initiated the conversion.
     * The [contextType] is the context in which the conversion takes place.
     * The [field] is set if the conversion happens in the context of a [Field].
     */
    abstract fun toTheirs(value: O?, agent: CopperfieldAgent, ourType: Class<out O>, contextType: Class<out Any>, field: Field?): T?

    /**
     * Transforms the [value] from [T] to [O].
     * The concrete type [T] to convert _to_ is defined in [ourType].
     * The [agent] is the [CopperfieldAgent] which initiated the conversion.
     * The [contextType] is the context in which the conversion takes place.
     * The [field] is set if the conversion happens in the context of a [Field].
     */
    abstract fun toOurs(value: T?, agent: CopperfieldAgent, ourType: Class<out O>, contextType: Class<out Any>, field: Field?): O?

}
