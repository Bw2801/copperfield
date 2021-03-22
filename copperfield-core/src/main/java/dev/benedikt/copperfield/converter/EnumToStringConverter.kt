package dev.benedikt.copperfield.converter

import dev.benedikt.copperfield.CopperfieldAgent
import java.lang.reflect.Field

/**
 * Converts [Enum]s to strings using the enum names and vice versa.
 *
 * @author Benedikt Wüller
 */
class EnumToStringConverter : Converter<Enum<*>, String>(Enum::class.java, String::class.java) {

    override fun toTheirs(value: Enum<*>?, agent: CopperfieldAgent, ourType: Class<out Enum<*>>, contextType: Class<out Any>, field: Field?): String? {
        return value?.name
    }

    override fun toOurs(value: String?, agent: CopperfieldAgent, ourType: Class<out Enum<*>>, contextType: Class<out Any>, field: Field?): Enum<*>? {
        if (value == null) return null
        val method = ourType.getDeclaredMethod("valueOf", String::class.java)
        return method.invoke(null, value) as Enum<*>
    }

}
