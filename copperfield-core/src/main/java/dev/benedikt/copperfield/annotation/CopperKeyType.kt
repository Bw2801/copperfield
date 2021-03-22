package dev.benedikt.copperfield.annotation

import kotlin.reflect.KClass

/**
 * Provides the generic key type for this field.
 *
 * @author Benedikt Wüller
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class CopperKeyType(val type: KClass<*>)
