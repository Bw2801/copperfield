package dev.volix.rewinside.odyssey.common.copperfield.proto.converter

import com.google.protobuf.ByteString
import dev.volix.rewinside.odyssey.common.copperfield.CopperfieldAgent
import dev.volix.rewinside.odyssey.common.copperfield.converter.Converter
import java.lang.reflect.Field

/**
 * @author Benedikt Wüller
 */
class ByteArrayToProtoByteStringConverter : Converter<ByteArray, ByteString>(ByteArray::class.java, ByteString::class.java) {

    override fun toTheirs(
        value: ByteArray?, agent: CopperfieldAgent, ourType: Class<out ByteArray>, targetFormat: Class<out Any>,
        field: Field?): ByteString? {
        if (value == null) return null
        return ByteString.copyFrom(value)
    }

    override fun toOurs(
        value: ByteString?, agent: CopperfieldAgent, ourType: Class<out ByteArray>, targetFormat: Class<out Any>,
        field: Field?): ByteArray? {
        if (value == null) return null
        return value.toByteArray()
    }

}
