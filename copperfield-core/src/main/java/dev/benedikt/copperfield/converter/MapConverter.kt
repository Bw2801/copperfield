package dev.benedikt.copperfield.converter

import dev.benedikt.copperfield.CopperfieldAgent
import dev.benedikt.copperfield.KeyAware
import dev.benedikt.copperfield.ValueAware
import dev.benedikt.copperfield.annotation.CopperMapType
import java.lang.reflect.Field

/**
 * Converts [Map]s to a new [Map] with transformed keys and values based on the defined types in the optional
 * [dev.benedikt.copperfield.annotation.CopperKeyType] and
 * [dev.benedikt.copperfield.annotation.CopperValueType] annotations.
 *
 * @author Benedikt Wüller
 */
class MapConverter : Converter<Map<*, *>, Map<*, *>>(Map::class.java, Map::class.java), KeyAware, ValueAware {

    override fun toTheirs(value: Map<*, *>?, agent: CopperfieldAgent, ourType: Class<out Map<*, *>>, contextType: Class<out Any>, field: Field?): Map<*, *> {
        if (value == null) return mapOf<Any, Any>()

        val valueType = this.getValueType(field)
        val keyType = this.getKeyType(field)

        return value
            .mapKeys { agent.toTheirs(it.key, keyType, contextType, field) }
            .mapValues { agent.toTheirs(it.value, valueType, contextType, field) }
    }

    override fun toOurs(value: Map<*, *>?, agent: CopperfieldAgent, ourType: Class<out Map<*, *>>, contextType: Class<out Any>, field: Field?): Map<*, *> {
        if (value == null) return mapOf<Any, Any>()

        val valueType = this.getValueType(field)
        val keyType = this.getKeyType(field)

        val convertedMap = value
            .mapKeys { agent.toOurs(it.key, keyType, contextType, field) }
            .mapValues { agent.toOurs(it.value, valueType, contextType, field) }

        val map = this.getMapType(field).newInstance()
        map.putAll(convertedMap as Map<out Any, Any?>)
        return map
    }

    private fun getMapType(field: Field?): Class<out MutableMap<Any, Any?>> {
        val annotation = field?.getDeclaredAnnotation(dev.benedikt.copperfield.annotation.CopperMapType::class.java)
        if (annotation != null) return annotation.type.java as Class<out MutableMap<Any, Any?>>
        return HashMap::class.java as Class<out MutableMap<Any, Any?>>
    }

}
