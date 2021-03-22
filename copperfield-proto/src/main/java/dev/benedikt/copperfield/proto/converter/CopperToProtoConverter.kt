package dev.benedikt.copperfield.proto.converter

import com.google.common.cache.CacheBuilder
import com.google.protobuf.GeneratedMessageV3
import com.google.protobuf.MessageLiteOrBuilder
import com.google.protobuf.Struct
import com.google.protobuf.StructOrBuilder
import com.google.protobuf.Value
import dev.benedikt.copperfield.CopperConvertable
import dev.benedikt.copperfield.CopperTypeMapper
import dev.benedikt.copperfield.converter.Converter
import dev.benedikt.copperfield.converter.CopperConvertableConverter
import dev.benedikt.copperfield.helper.convertToType
import dev.benedikt.copperfield.helper.getAnnotation
import dev.benedikt.copperfield.helper.getOrPut
import dev.benedikt.copperfield.helper.snakeToPascalCase
import dev.benedikt.copperfield.proto.annotation.CopperProtoClass
import dev.benedikt.copperfield.proto.annotation.CopperProtoField
import dev.benedikt.copperfield.proto.helper.convertFromStructValue
import dev.benedikt.copperfield.proto.helper.convertFromValue
import dev.benedikt.copperfield.proto.helper.convertToStructValue
import dev.benedikt.copperfield.proto.helper.convertToValue
import dev.benedikt.copperfield.proto.helper.getBaseValueType
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.concurrent.TimeUnit

/**
 * Converts [CopperConvertable] instances to [MessageLiteOrBuilder] instances.
 *
 * @see CopperConvertableConverter
 *
 * @author Benedikt Wüller
 */
class CopperToProtoConverter : CopperConvertableConverter<MessageLiteOrBuilder>(MessageLiteOrBuilder::class.java) {

    /**
     * Cached methods used to receive the value from the [MessageLiteOrBuilder].
     */
    private val getterMethods = CacheBuilder.newBuilder()
        .expireAfterAccess(5, TimeUnit.MINUTES)
        .build<Triple<String, Class<*>, Class<*>>, Method>()

    /**
     * Cached methods used to receive the value from the [MessageLiteOrBuilder].
     */
    private val hasMethods = CacheBuilder.newBuilder()
        .expireAfterAccess(5, TimeUnit.MINUTES)
        .build<Triple<String, Class<*>, Class<*>>, Method?>()

    /**
     * Cached methods used to provide the value for the [MessageLiteOrBuilder].
     */
    private val setterMethods = CacheBuilder.newBuilder()
        .expireAfterAccess(5, TimeUnit.MINUTES)
        .build<Triple<String, Class<*>, Class<*>>, Method>()

    override fun createTheirInstance(type: Class<out Any>, ourType: Class<out CopperConvertable>?): MessageLiteOrBuilder {
        val annotation = if (ourType == null) null else getAnnotation(ourType, CopperProtoClass::class.java)
        val protoType = annotation?.type?.java ?: type

        // Create a builder instead of the final instance, because ProtoBuf only supports the builder pattern.
        val newBuilderMethod = protoType.getDeclaredMethod("newBuilder")
        return newBuilderMethod.invoke(null) as GeneratedMessageV3.Builder<*>
    }

    override fun finalizeTheirInstance(instance: MessageLiteOrBuilder): MessageLiteOrBuilder {
        return instance.javaClass.getDeclaredMethod("build").invoke(instance) as MessageLiteOrBuilder
    }

    override fun getValue(instance: MessageLiteOrBuilder, name: String, type: Class<*>): Any? {
        val exists = this.getHasMethod(name, instance.javaClass, type)
        val getter = this.getGetterMethod(name, instance.javaClass, type)

        if (instance is StructOrBuilder) {
            val value = convertFromStructValue(getter.invoke(instance, name, null) as Value?)
            if (value is Number && Number::class.java.isAssignableFrom(type)) {
                return value.convertToType(type as Class<out Number>)
            }
            return value
        } else {
            if (exists == null || exists.invoke(instance) == true) {
                return convertFromValue(getter.invoke(instance))
            }
            return null
        }
    }

    override fun setValue(instance: MessageLiteOrBuilder, name: String, value: Any?, type: Class<*>) {
        val correctedType = if (type == Map::class.java && value is Struct) Struct::class.java else type
        val method = this.getSetterMethod(name, instance.javaClass, correctedType)

        if (instance is StructOrBuilder) {
            method.invoke(instance, name, convertToStructValue(value))
        } else {
            val valueType = getBaseValueType(type)
            if (valueType != null && method.parameters.first().type.isAssignableFrom(valueType)) {
                val convertedValue = convertToValue(value) ?: return
                method.invoke(instance, convertedValue)
            } else {
                method.invoke(instance, value)
            }
        }
    }

    /**
     * Finds and returns the method used to retrieve a value of [type] with the given [name] from the [entityType].
     * Caches the method for further use.
     */
    private fun getHasMethod(name: String, entityType: Class<out Any>, type: Class<out Any>): Method? {
        val key = Triple(name, type, entityType)
        return this.hasMethods.getOrPut(key) {
            return@getOrPut try {
                entityType.getDeclaredMethod("has${name.snakeToPascalCase()}")
            } catch (ex: Exception) {
                null
            }
        }
    }

    /**
     * Finds and returns the method used to retrieve a value of [type] with the given [name] from the [entityType].
     * Caches the method for further use.
     */
    private fun getGetterMethod(name: String, entityType: Class<out Any>, type: Class<out Any>): Method {
        val key = Triple(name, type, entityType)
        return this.getterMethods.getOrPut(key) {
            if (StructOrBuilder::class.java.isAssignableFrom(entityType)) {
                return@getOrPut entityType.getDeclaredMethod("getFieldsOrDefault", String::class.java, Value::class.java)
            }

            val methodName = when {
                Iterable::class.java.isAssignableFrom(type) -> "get${name.snakeToPascalCase()}List"
                else -> "get${name.snakeToPascalCase()}"
            }

            return@getOrPut entityType.getDeclaredMethod(methodName)
        }
    }

    /**
     * Finds and returns the method used to provide a value of [type] with the given [name] for the [entityType].
     * Caches the method for further use.
     */
    private fun getSetterMethod(name: String, entityType: Class<out MessageLiteOrBuilder>, type: Class<out Any>): Method {
        val key = Triple(name, type, entityType)
        return this.setterMethods.getOrPut(key) {
            if (StructOrBuilder::class.java.isAssignableFrom(entityType)) {
                return@getOrPut entityType.getDeclaredMethod("putFields", String::class.java, Value::class.java)
            }

            val methodName = when {
                Iterable::class.java.isAssignableFrom(type) -> "addAll${name.snakeToPascalCase()}"
                else -> "set${name.snakeToPascalCase()}"
            }

            val valueType = getBaseValueType(type)
            return@getOrPut entityType.declaredMethods.firstOrNull { method ->
                if (method.name != methodName) return@firstOrNull false
                if (method.parameterCount != 1) return@firstOrNull false
                val parameter = method.parameterTypes.first()
                parameter.isAssignableFrom(type) || (valueType != null && parameter.isAssignableFrom(valueType))
            } ?: throw RuntimeException("Unable to find setter method for ${type.name}.")
        }
    }

    override fun getName(name: String, field: Field): String {
        val annotation = field.getDeclaredAnnotation(CopperProtoField::class.java)
        if (annotation != null && annotation.name.isNotEmpty()) return annotation.name
        return super.getName(name, field)
    }

    override fun getConverterType(type: Class<out Converter<out Any, out Any>>, field: Field): Class<out Converter<out Any, out Any>> {
        val annotation = field.getDeclaredAnnotation(CopperProtoField::class.java)
        if (annotation != null && annotation.converter != Converter::class) return annotation.converter.java
        return super.getConverterType(type, field)
    }

    override fun getTypeMapper(type: Class<out CopperTypeMapper<out CopperConvertable, out CopperConvertable>>, field: Field): Class<out CopperTypeMapper<out CopperConvertable, out CopperConvertable>> {
        val annotation = field.getDeclaredAnnotation(CopperProtoField::class.java)
        if (annotation != null && annotation.typeMapper != CopperTypeMapper::class) return annotation.typeMapper.java
        return super.getTypeMapper(type, field)
    }

    override fun getMappedContextType(type: Class<out CopperConvertable>, contextType: Class<out Any>): Class<out Any> {
        val annotation = getAnnotation(type, CopperProtoClass::class.java)
        return annotation?.type?.java ?: contextType
    }

}
