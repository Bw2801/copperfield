package dev.benedikt.copperfield.proto.converter

import com.google.protobuf.Struct
import dev.benedikt.copperfield.CopperfieldAgent
import dev.benedikt.copperfield.converter.Converter
import dev.benedikt.copperfield.converter.MapConverter
import dev.benedikt.copperfield.proto.helper.convertMapToStruct
import dev.benedikt.copperfield.proto.helper.convertStructToMap
import java.lang.reflect.Field

/**
 * Converts maps to/from [Struct]. The keys _must_ be a string. The following value types are supported:
 *   - [Number]
 *   - [String]
 *   - [Boolean]
 *   - [Map] following the same restrictions.
 *   - [Iterable] following the same restrictions (value types).
 *
 * Values of type [com.google.protobuf.Value] and [Struct] are allowed as well but will be (recursively) converted to one
 * of the types above when converting [toOurs].
 *
 * If you need to support rich or custom key/value types, define a custom [Converter] using either the
 * [dev.benedikt.copperfield.annotation.CopperField] or
 * [dev.benedikt.copperfield.proto.annotation.CopperProtoField] annotation.
 *
 * @author Benedikt Wüller
 */
class MapToProtoStructConverter : Converter<Map<*, *>, Struct>(Map::class.java, Struct::class.java) {

    override fun toTheirs(value: Map<*, *>?, agent: CopperfieldAgent, ourType: Class<out Map<*, *>>, contextType: Class<out Any>, field: Field?): Struct? {
        val map = agent.toTheirsWithConverter(value, ourType, MapConverter::class.java, contextType, field) ?: return null
        return convertMapToStruct(map as Map<*, *>)
    }

    override fun toOurs(value: Struct?, agent: CopperfieldAgent, ourType: Class<out Map<*, *>>, contextType: Class<out Any>, field: Field?): Map<*, *>? {
        val map = if (value == null) null else convertStructToMap(value)
        return agent.toOursWithConverter(map, ourType, MapConverter::class.java, contextType, field)
    }

}
