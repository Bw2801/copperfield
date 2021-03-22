package dev.benedikt.copperfield.converter

import dev.benedikt.copperfield.CopperfieldAgent
import dev.benedikt.copperfield.helper.convertToType
import java.lang.reflect.Field

/**
 * Makes sure the [Number]s are converted to the right type when converting `theirs` to `ours`.

 * @author Benedikt Wüller
 */
class NumberConverter : Converter<Number, Number>(Number::class.java, Number::class.java) {

    override fun toTheirs(value: Number?, agent: CopperfieldAgent, ourType: Class<out Number>, contextType: Class<out Any>, field: Field?) = value

    override fun toOurs(value: Number?, agent: CopperfieldAgent, ourType: Class<out Number>, contextType: Class<out Any>, field: Field?): Number? {
        return value?.convertToType(ourType)
    }

}
