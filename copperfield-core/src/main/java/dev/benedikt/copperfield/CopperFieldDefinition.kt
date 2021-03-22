package dev.benedikt.copperfield

import dev.benedikt.copperfield.converter.Converter
import java.lang.reflect.Field

/**
 * The internal definition of a convertable field.
 *
 * @author Benedikt Wüller
 */
data class CopperFieldDefinition(val field: Field, val name: String,
                                 val converter: Class<out Converter<out Any, out Any>>,
                                 val typeMapper: CopperTypeMapper<out CopperConvertable, out CopperConvertable>?)
