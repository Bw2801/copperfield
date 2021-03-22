package dev.benedikt.copperfield.proto.registry

import com.google.protobuf.MessageLiteOrBuilder
import dev.benedikt.copperfield.CopperConvertable
import dev.benedikt.copperfield.proto.converter.ByteArrayToProtoByteStringConverter
import dev.benedikt.copperfield.proto.converter.CopperToProtoConverter
import dev.benedikt.copperfield.proto.converter.MapToProtoStructConverter
import dev.benedikt.copperfield.proto.registry.ProtoRegistry.Companion.PROTO_CONTEXT
import dev.benedikt.copperfield.registry.Registry

/**
 * Provides the default [dev.benedikt.copperfield.converter.Converter]s for the proto context:
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
        this.with(CopperConvertable::class.java, CopperToProtoConverter::class.java, PROTO_CONTEXT)
        this.with(ByteArray::class.java, ByteArrayToProtoByteStringConverter::class.java, PROTO_CONTEXT)
        this.with(Map::class.java, MapToProtoStructConverter::class.java, PROTO_CONTEXT)
    }

}
