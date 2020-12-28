package dev.volix.rewinside.odyssey.common.copperfield.annotation

/**
 * @author Benedikt Wüller
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class CopperField(val name: String = "")
