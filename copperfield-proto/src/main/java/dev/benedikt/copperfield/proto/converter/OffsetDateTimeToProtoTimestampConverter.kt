package dev.benedikt.copperfield.proto.converter

import com.google.protobuf.Timestamp
import dev.benedikt.copperfield.CopperfieldAgent
import dev.benedikt.copperfield.converter.Converter
import java.lang.reflect.Field
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId

/**
 * Converts [OffsetDateTime]s to/from [Timestamp]s using the given [timeZone].
 * All dates passed to this converter are assumed to share the [timeZone].
 *
 * @author Benedikt Wüller
 */
class OffsetDateTimeToProtoTimestampConverter(private val timeZone: ZoneId = ZoneId.of("CET")) : Converter<OffsetDateTime, Timestamp>(OffsetDateTime::class.java, Timestamp::class.java) {

    override fun toTheirs(value: OffsetDateTime?, agent: CopperfieldAgent, ourType: Class<out OffsetDateTime>, contextType: Class<out Any>, field: Field?): Timestamp? {
        val instant = value?.toInstant() ?: return null
        return Timestamp.newBuilder()
            .setSeconds(instant.epochSecond)
            .setNanos(instant.nano)
            .build()
    }

    override fun toOurs(value: Timestamp?, agent: CopperfieldAgent, ourType: Class<out OffsetDateTime>, contextType: Class<out Any>, field: Field?): OffsetDateTime? {
        if (value == null) return null
        return OffsetDateTime.ofInstant(Instant.ofEpochSecond(value.seconds, value.nanos.toLong()), this.timeZone)
    }

}
