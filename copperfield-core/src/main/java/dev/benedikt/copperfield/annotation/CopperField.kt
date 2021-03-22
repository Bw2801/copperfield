package dev.benedikt.copperfield.annotation

import dev.benedikt.copperfield.CopperConvertable
import dev.benedikt.copperfield.CopperTypeMapper
import dev.benedikt.copperfield.converter.Converter
import kotlin.reflect.KClass

/**
 * Flags this field to be included in the conversion process of a [CopperConvertable] class.
 *
 * The [name] will be used as basis for the target name and may be modified to fit conventions based on the implementation.
 *
 * If the [name] is an empty string, the java field name will be used and converted to snake case.
 *
 * If a [converter] is set, it will override the default behavior for this field. Otherwise the default converter defined in the
 * [dev.benedikt.copperfield.CopperfieldAgent] will be used, if there is any.
 *
 * If a [typeMapper] is set, it wil be called to determine the concrete type to convert to and from when converting this field.
 *
 * @see dev.benedikt.copperfield.CopperfieldAgent
 * @see CopperConvertable
 * @see dev.benedikt.copperfield.converter.CopperConvertableConverter
 *
 * @author Benedikt Wüller
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class CopperField(val name: String = "",
                             val converter: KClass<out Converter<out Any, out Any>> = Converter::class,
                             val typeMapper: KClass<out CopperTypeMapper<out CopperConvertable, out CopperConvertable>> = CopperTypeMapper::class)
