package dev.benedikt.copperfield.bson.annotation

import dev.benedikt.copperfield.CopperConvertable
import dev.benedikt.copperfield.CopperTypeMapper
import dev.benedikt.copperfield.converter.Converter
import kotlin.reflect.KClass

/**
 * Equals the functionality of [dev.benedikt.copperfield.annotation.CopperField]s but can override one or multiple arguments
 * of the [dev.benedikt.copperfield.annotation.CopperField] for the bson context only.
 *
 * @see dev.benedikt.copperfield.annotation.CopperField
 *
 * @author Benedikt Wüller
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class CopperBsonField(val name: String = "",
                                 val converter: KClass<out Converter<out Any, out Any>> = Converter::class,
                                 val typeMapper: KClass<out CopperTypeMapper<out CopperConvertable, out CopperConvertable>> = CopperTypeMapper::class)
