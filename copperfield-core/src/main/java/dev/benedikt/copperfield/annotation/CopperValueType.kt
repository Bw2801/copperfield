package dev.benedikt.copperfield.annotation

import kotlin.reflect.KClass

/**
 * Provides the generic value type for this field.
 *
 * @author Benedikt Wüller
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class CopperValueType(val type: KClass<*>)
