package dev.volix.rewinside.odyssey.common.copperfield.bson

import dev.volix.rewinside.odyssey.common.copperfield.ConverterRegistry
import dev.volix.rewinside.odyssey.common.copperfield.bson.converter.ByteArrayBsonConverter
import dev.volix.rewinside.odyssey.common.copperfield.bson.converter.ConvertibleBsonConverter
import dev.volix.rewinside.odyssey.common.copperfield.bson.converter.NumberBsonConverter
import dev.volix.rewinside.odyssey.common.copperfield.bson.converter.SimpleBsonConverter
import dev.volix.rewinside.odyssey.common.copperfield.bson.converter.UuidBsonConverter
import org.bson.Document
import java.util.UUID

/**
 * @author Benedikt Wüller
 */
class BsonRegistry : ConverterRegistry<Document>() {

    // TODO
    //  - list

    init {
        this.defaultConverter = SimpleBsonConverter()

        this.registerConverter(Number::class.java, NumberBsonConverter())
        this.registerConverter(UUID::class.java, UuidBsonConverter())
        this.registerConverter(ByteArray::class.java, ByteArrayBsonConverter())
        this.registerConverter(BsonConvertible::class.java, ConvertibleBsonConverter())
    }

}
