package dev.volix.rewinside.odyssey.common.copperfield.converter

import dev.volix.rewinside.odyssey.common.copperfield.annotation.CopperKeyType
import java.lang.reflect.Field

/**
 * @author Benedikt Wüller
 */
interface KeyAware {

    @JvmDefault
    fun getKeyType(field: Field?): Class<*> {
        val annotation = field?.getDeclaredAnnotation(CopperKeyType::class.java)
        // TODO: use type mapper
        return annotation?.type?.java ?: Any::class.java
    }

}
