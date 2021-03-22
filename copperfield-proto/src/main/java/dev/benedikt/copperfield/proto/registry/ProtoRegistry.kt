package dev.benedikt.copperfield.proto.registry

import com.google.protobuf.MessageLiteOrBuilder
import dev.benedikt.copperfield.CopperConvertable
import dev.volix.rewinside.odyssey.common.copperfield.proto.converter.ByteArrayToProtoByteStringConverter
import dev.volix.rewinside.odyssey.common.copperfield.proto.converter.CopperToProtoConverter
import dev.volix.rewinside.odyssey.common.copperfield.proto.converter.MapToProtoStructConverter
import dev.volix.rewinside.odyssey.common.copperfield.proto.registry.ProtoRegistry.Companion.PROTO_CONTEXT
import dev.volix.rewinside.odyssey.common.copperfield.registry.Registry

/**
 * Provides the default [dev.volix.rewinside.odyssey.common.copperfield.converter.Converter]s for the proto context:
 *  - [ByteArray] using [ByteArrayToProtoByteStringConverter]
 *  - [CopperConvertable] using [CopperToProtoConverter]
 *  - [Map] using [MapToProtoStructConverter]
 *
 * Use [PROTO_CONTEXT] in the [Registry] methods to convert using the proto context.
 *
 * @author Benedikt Wüller
 */
class ProtoRegistry : Registry() {

    companion object {
        @JvmField
        val PROTO_CONTEXT = MessageLiteOrBuilder::class.java
    }

    init {
        this.with(dev.benedikt.copperfield.CopperConvertable::class.java, CopperToProtoConverter::class.java, PROTO_CONTEXT)
        this.with(ByteArray::class.java, ByteArrayToProtoByteStringConverter::class.java, PROTO_CONTEXT)
        this.with(Map::class.java, MapToProtoStructConverter::class.java, PROTO_CONTEXT)
    }

}
