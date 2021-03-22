package dev.benedikt.copperfield.bson.converter

import dev.benedikt.copperfield.CopperfieldAgent
import dev.benedikt.copperfield.converter.Converter
import org.bson.types.Binary
import java.lang.reflect.Field

/**
 * Converts byte arrays to/from bson [Binary] objects.
 *
 * @author Benedikt Wüller
 */
class ByteArrayToBsonBinaryConverter : Converter<ByteArray, Binary>(ByteArray::class.java, Binary::class.java) {

    override fun toTheirs(value: ByteArray?, agent: CopperfieldAgent, ourType: Class<out ByteArray>, contextType: Class<out Any>, field: Field?): Binary? {
        if (value == null) return null
        return Binary(value)
    }

    override fun toOurs(value: Binary?, agent: CopperfieldAgent, ourType: Class<out ByteArray>, contextType: Class<out Any>, field: Field?): ByteArray? {
        if (value == null) return null
        return value.data
    }

}
