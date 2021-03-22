package dev.benedikt.copperfield.converter

import dev.benedikt.copperfield.CopperfieldAgent
import java.lang.reflect.Field
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

/**
 * Converts [OffsetDateTime]s to strings using the given [formatter] which defaults to [DateTimeFormatter.ISO_OFFSET_DATE_TIME].
 *
 * @author Benedikt Wüller
 */
class OffsetDateTimeToStringConverter(private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    : Converter<OffsetDateTime, String>(OffsetDateTime::class.java, String::class.java) {

    override fun toTheirs(
        value: OffsetDateTime?, agent: CopperfieldAgent, ourType: Class<out OffsetDateTime>, contextType: Class<out Any>,
        field: Field?): String? {
        return value?.format(this.formatter)
    }

    override fun toOurs(
        value: String?, agent: CopperfieldAgent, ourType: Class<out OffsetDateTime>, contextType: Class<out Any>,
        field: Field?): OffsetDateTime? {
        return if (value == null) null else OffsetDateTime.parse(value, this.formatter)
    }

}
