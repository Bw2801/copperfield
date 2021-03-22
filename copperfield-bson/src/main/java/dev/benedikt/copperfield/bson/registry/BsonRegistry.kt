package dev.benedikt.copperfield.bson.registry

import dev.benedikt.copperfield.CopperConvertable
import dev.benedikt.copperfield.bson.converter.BsonObjectIdToStringConverter
import dev.benedikt.copperfield.bson.converter.ByteArrayToBsonBinaryConverter
import dev.benedikt.copperfield.bson.converter.CopperToBsonConverter
import dev.benedikt.copperfield.bson.registry.BsonRegistry.Companion.BSON_CONTEXT
import dev.volix.rewinside.odyssey.common.copperfield.converter.NoOperationConverter
import dev.volix.rewinside.odyssey.common.copperfield.registry.Registry
import org.bson.Document
import org.bson.types.ObjectId

/**
 * Provides the default [dev.volix.rewinside.odyssey.common.copperfield.converter.Converter]s for the bson context:
 *  - [ByteArray] using [ByteArrayToBsonBinaryConverter]
 *  - [CopperConvertable] using [CopperToBsonConverter]
 *
 * It also provides default converters for cross conversion without the bson context:
 *  - [ObjectId] using [BsonObjectIdToStringConverter]
 *
 * Use [BSON_CONTEXT] in the [Registry] methods to convert using the bson context.
 *
 * @author Benedikt Wüller
 */
class BsonRegistry : Registry() {

    companion object {
        @JvmField
        val BSON_CONTEXT = Document::class.java
    }

    init {
        this.with(ByteArray::class.java, ByteArrayToBsonBinaryConverter::class.java, BSON_CONTEXT)
        this.with(dev.benedikt.copperfield.CopperConvertable::class.java, CopperToBsonConverter::class.java, BSON_CONTEXT)
        this.with(ObjectId::class.java, NoOperationConverter::class.java, BSON_CONTEXT)

        // Support for cross conversions.
        this.with(ObjectId::class.java, BsonObjectIdToStringConverter::class.java)
    }

}
