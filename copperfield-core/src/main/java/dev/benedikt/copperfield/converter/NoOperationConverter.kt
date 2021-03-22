package dev.benedikt.copperfield.converter

import dev.volix.rewinside.odyssey.common.copperfield.CopperfieldAgent
import java.lang.reflect.Field

/**
 * Does not change the value at all.
 *
 * @author Benedikt Wüller
 */
class NoOperationConverter : Converter<Any, Any>(Any::class.java, Any::class.java) {

    override fun toTheirs(value: Any?, agent: CopperfieldAgent, ourType: Class<out Any>, contextType: Class<out Any>, field: Field?) = value

    override fun toOurs(value: Any?, agent: CopperfieldAgent, ourType: Class<out Any>, contextType: Class<out Any>, field: Field?) = value

}
