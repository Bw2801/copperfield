package dev.benedikt.copperfield

import dev.volix.rewinside.odyssey.common.copperfield.annotation.CopperValueType
import java.lang.reflect.Field

/**
 * Provides the generic helper function [getValueType].
 *
 * @author Benedikt Wüller
 */
interface ValueAware {

    /**
     * Returns the type defined in the [CopperValueType] annotation on the given [field]. If there is no annotation present, type [Any] will be
     * returned.
     */
    @JvmDefault
    fun getValueType(field: Field?): Class<*> {
        val annotation = field?.getDeclaredAnnotation(CopperValueType::class.java)
        return annotation?.type?.java ?: Any::class.java
    }

}
