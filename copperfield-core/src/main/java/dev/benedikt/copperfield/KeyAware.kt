package dev.benedikt.copperfield

import dev.volix.rewinside.odyssey.common.copperfield.annotation.CopperKeyType
import java.lang.reflect.Field

/**
 * Provides the generic helper function [getKeyType].
 *
 * @author Benedikt Wüller
 */
interface KeyAware {

    /**
     * Returns the type defined in the [CopperKeyType] annotation on the given [field]. If there is no annotation present, type [Any] will be
     * returned.
     */
    @JvmDefault
    fun getKeyType(field: Field?): Class<*> {
        val annotation = field?.getDeclaredAnnotation(CopperKeyType::class.java)
        return annotation?.type?.java ?: Any::class.java
    }

}
