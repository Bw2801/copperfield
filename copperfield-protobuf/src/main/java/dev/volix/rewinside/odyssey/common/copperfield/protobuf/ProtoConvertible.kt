package dev.volix.rewinside.odyssey.common.copperfield.protobuf

import com.google.protobuf.GeneratedMessageV3
import dev.volix.rewinside.odyssey.common.copperfield.convertFrom
import dev.volix.rewinside.odyssey.common.copperfield.convertTo

/**
 * @author Benedikt Wüller
 */
interface ProtoConvertible<T : GeneratedMessageV3> {

    @JvmDefault
    fun toProtoMessage(type: Class<T>, registry: ProtoRegistry): T {
        val newBuilderMethod = type.getDeclaredMethod("newBuilder")
        val builder = newBuilderMethod.invoke(null) as GeneratedMessageV3.Builder<*>

        convertTo(this, builder, registry, ProtoFieldNameMapper, ProtoFieldFilter)

        val buildMethod = builder.javaClass.getDeclaredMethod("build")
        return buildMethod.invoke(builder) as T
    }

    @JvmDefault
    fun fromProtoMessage(source: T, registry: ProtoRegistry) {
        convertFrom(this, source, registry, ProtoFieldNameMapper, ProtoFieldFilter)
    }

}
