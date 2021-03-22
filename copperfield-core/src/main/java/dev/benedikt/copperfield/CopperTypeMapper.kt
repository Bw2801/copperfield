package dev.benedikt.copperfield

import java.lang.reflect.Field

/**
 * Type mappers provide the ability to return a specific type assignable from [T] based on the given [Parent] instance.
 *
 * They define, which [requiredFields] have to be set in the [Parent] instance before the mapper is able to decide on the returned type.
 *
 * @author Benedikt Wüller
 */
abstract class CopperTypeMapper<Parent : CopperConvertable, T : CopperConvertable>(private vararg val requiredFields: String) {

    /**
     * Maps the given [valueType] based on the populated instance based on the [requiredFields].
     */
    abstract fun mapType(instance: Parent, valueType: Class<out Any>): Class<out T>

    /**
     * Returns whether the given [field] is contained withing the [requiredFields].
     */
    fun requires(field: Field) = this.requiredFields.contains(field.name)

}
