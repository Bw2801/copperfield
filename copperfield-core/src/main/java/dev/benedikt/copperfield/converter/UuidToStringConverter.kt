package dev.benedikt.copperfield.converter

import dev.volix.rewinside.odyssey.common.copperfield.CopperfieldAgent
import java.lang.reflect.Field
import java.util.UUID

/**
 * Converts [UUID]s to strings and vice versa.
 *
 * @author Benedikt Wüller
 */
class UuidToStringConverter : Converter<UUID, String>(UUID::class.java, String::class.java) {

    override fun toTheirs(value: UUID?, agent: CopperfieldAgent, ourType: Class<out UUID>, contextType: Class<out Any>, field: Field?): String? {
        return value?.toString()
    }

    override fun toOurs(value: String?, agent: CopperfieldAgent, ourType: Class<out UUID>, contextType: Class<out Any>, field: Field?): UUID? {
        if (value == null) return null
        return UUID.fromString(value)
    }

}
