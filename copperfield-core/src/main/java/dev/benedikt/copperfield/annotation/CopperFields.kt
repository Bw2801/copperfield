package dev.benedikt.copperfield.annotation

/**
 * Flags this class and all it's derived classes to indicate that all declared fields will be treated as being annotated with [CopperField] by
 * default. This still allows to annotate [CopperField] manually to override default functionality if desired.
 *
 * To exclude a given field, use the [CopperIgnore] annotation.
 *
 * @see CopperField
 * @see CopperIgnore
 * @see dev.volix.rewinside.odyssey.common.copperfield.converter.CopperConvertableConverter
 *
 * @author Benedikt Wüller
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class CopperFields
