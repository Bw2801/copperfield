package dev.benedikt.copperfield.proto.helper

import com.google.protobuf.BoolValue
import com.google.protobuf.DoubleValue
import com.google.protobuf.FloatValue
import com.google.protobuf.Int32Value
import com.google.protobuf.Int64Value
import com.google.protobuf.ListValue
import com.google.protobuf.NullValue
import com.google.protobuf.StringValue
import com.google.protobuf.Struct
import com.google.protobuf.Value
import dev.volix.rewinside.odyssey.common.copperfield.exception.CopperFieldException

/**
 * @author Benedikt Wüller
 */

/**
 * Recursively converts [map] values to [Value]s.
 * Returns a new map.
 */
fun convertMapToStruct(map: Map<*, *>): Struct {
    return Struct.newBuilder().putAllFields(
        map.mapKeys { (key, _) ->
            if (key !is String) throw CopperFieldException("Expected key of type string in map. Found ${key?.javaClass}.")
            return@mapKeys key
        }.mapValues { convertToStructValue(it.value) }
    ).build()
}

/**
 * Recursively converts the [struct] to a map.
 * Returns a new map.
 */
fun convertStructToMap(struct: Struct): Map<String, Any?> {
    return struct.fieldsMap.mapValues { (_, value) -> convertFromStructValue(value, true) }
}

/**
 * Recursively converts the [iterable] to a [ListValue].
 */
fun convertIterableToListValue(iterable: Iterable<*>): ListValue? {
    return ListValue.newBuilder().addAllValues(iterable.map(::convertToStructValue)).build()
}

/**
 * Recursively converts the [value] to an iterable.
 */
fun convertListValueToIterable(value: ListValue): Iterable<Any?> {
    return value.valuesList.map(::convertFromStructValue)
}

/**
 * Converts the [value] to a single [Value] or [Struct] (potentially recursively).
 */
fun convertToStructValue(value: Any?): Value {
    val builder = Value.newBuilder()
    when (value) {
        null -> builder.nullValue = NullValue.NULL_VALUE
        is Number -> builder.numberValue = value.toDouble()
        is String -> builder.stringValue = value
        is Boolean -> builder.boolValue = value
        is Struct -> builder.structValue = value
        is Map<*, *> -> builder.structValue = convertMapToStruct(value)
        is Iterable<*> -> builder.listValue = convertIterableToListValue(value)
        is Value -> return value
        else -> throw CopperFieldException("Invalid struct value ${value.javaClass}.")
    }
    return builder.build()
}

/**
 * Converts the [value] to an object of the corresponding type (potentially recursively).
 */
fun convertFromStructValue(value: Value?, structToMap: Boolean = false): Any? {
    if (value == null) return null
    return when (value.kindCase) {
        Value.KindCase.NULL_VALUE -> null
        Value.KindCase.NUMBER_VALUE -> value.numberValue
        Value.KindCase.STRING_VALUE -> value.stringValue
        Value.KindCase.BOOL_VALUE -> value.boolValue
        Value.KindCase.STRUCT_VALUE -> if (structToMap) convertStructToMap(value.structValue) else value.structValue
        Value.KindCase.LIST_VALUE -> convertListValueToIterable(value.listValue)
        else -> throw CopperFieldException("KindCase ${value.kindCase} is not supported.")
    }
}

fun getBaseValueType(type: Class<*>): Class<*>? {
    return when {
        String::class.java.isAssignableFrom(type) -> StringValue::class.java
        Int::class.java.isAssignableFrom(type) -> Int32Value::class.java
        Long::class.java.isAssignableFrom(type) -> Int64Value::class.java
        Double::class.java.isAssignableFrom(type) -> DoubleValue::class.java
        Float::class.java.isAssignableFrom(type) -> FloatValue::class.java
        Boolean::class.java.isAssignableFrom(type) -> BoolValue::class.java
        else -> null
    }
}

fun convertToValue(value: Any?): Any? {
    return when (value) {
        is String -> StringValue.of(value)
        is Int -> Int32Value.of(value)
        is Long -> Int64Value.of(value)
        is Double -> DoubleValue.of(value)
        is Float -> FloatValue.of(value)
        is Boolean -> BoolValue.of(value)
        else -> null
    }
}

fun convertFromValue(value: Any?): Any? {
    if (value == null) return null
    return when (value) {
        is StringValue -> value.value
        is Int32Value -> value.value
        is Int64Value -> value.value
        is DoubleValue -> value.value
        is FloatValue -> value.value
        is BoolValue -> value.value
        is NullValue -> null
        else -> value
    }
}
