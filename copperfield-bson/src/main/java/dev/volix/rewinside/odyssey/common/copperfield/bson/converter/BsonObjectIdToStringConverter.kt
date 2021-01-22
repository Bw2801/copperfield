package dev.volix.rewinside.odyssey.common.copperfield.bson.converter

import dev.volix.rewinside.odyssey.common.copperfield.CopperfieldAgent
import dev.volix.rewinside.odyssey.common.copperfield.converter.Converter
import org.bson.types.ObjectId
import java.lang.reflect.Field

/**
 * Converts bson [ObjectId]s to/from their hex string representation.
 *
 * @author Benedikt Wüller
 */
class BsonObjectIdToStringConverter : Converter<ObjectId, String>(ObjectId::class.java, String::class.java) {

    override fun toTheirs(value: ObjectId?, agent: CopperfieldAgent, ourType: Class<out ObjectId>, contextType: Class<out Any>, field: Field?): String? {
        return value?.toHexString()
    }

    override fun toOurs(value: String?, agent: CopperfieldAgent, ourType: Class<out ObjectId>, contextType: Class<out Any>, field: Field?): ObjectId? {
        return if (value == null) null else ObjectId(value)
    }

}
