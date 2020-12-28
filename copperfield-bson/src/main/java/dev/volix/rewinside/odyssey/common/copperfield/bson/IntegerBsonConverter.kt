package dev.volix.rewinside.odyssey.common.copperfield.bson

import org.bson.Document

/**
 * @author Benedikt Wüller
 */
class IntegerBsonConverter : BsonConverter<Int> {
    override fun convertFrom(name: String, source: Document): Int? = source.getInteger(name)
}
