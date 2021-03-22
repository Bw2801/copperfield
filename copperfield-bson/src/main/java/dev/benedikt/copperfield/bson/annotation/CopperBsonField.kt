package dev.benedikt.copperfield.bson.annotation

import dev.benedikt.copperfield.CopperConvertable
import dev.volix.rewinside.odyssey.common.copperfield.CopperTypeMapper
import dev.volix.rewinside.odyssey.common.copperfield.converter.Converter
import kotlin.reflect.KClass

/**
 * Equals the functionality of [dev.volix.rewinside.odyssey.common.copperfield.annotation.CopperField]s but can override one or multiple arguments
 * of the [dev.volix.rewinside.odyssey.common.copperfield.annotation.CopperField] for the bson context only.
 *
 * @see dev.volix.rewinside.odyssey.common.copperfield.annotation.CopperField
 *
 * @author Benedikt Wüller
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class CopperBsonField(val name: String = "",
                                 val converter: KClass<out Converter<out Any, out Any>> = Converter::class,
                                 val typeMapper: KClass<out CopperTypeMapper<out dev.benedikt.copperfield.CopperConvertable, out dev.benedikt.copperfield.CopperConvertable>> = CopperTypeMapper::class)
