package dev.benedikt.copperfield.registry

import dev.benedikt.copperfield.converter.CollectionConverter
import dev.benedikt.copperfield.converter.EnumToStringConverter
import dev.benedikt.copperfield.converter.MapConverter
import dev.benedikt.copperfield.converter.NumberConverter
import dev.benedikt.copperfield.converter.OffsetDateTimeToStringConverter
import dev.benedikt.copperfield.converter.UuidToStringConverter
import java.time.OffsetDateTime
import java.util.UUID

/**
 * The base registry contains the default converters always present in every [dev.benedikt.copperfield.CopperfieldAgent] by
 * default. It consists of the following [dev.benedikt.copperfield.converter.Converter]s:
 *  - [Collection] using [CollectionConverter]
 *  - [Map] using [MapConverter]
 *  - [Number] using [NumberConverter]
 *  - [UUID] using [UuidToStringConverter]
 *  - [Enum] using [EnumToStringConverter]
 *  - [OffsetDateTime] using [OffsetDateTimeToStringConverter]
 *
 * @author Benedikt Wüller
 */
open class BaseRegistry : Registry() {

    init {
        this.with(Collection::class.java, CollectionConverter::class.java)
        this.with(Map::class.java, MapConverter::class.java)
        this.with(Number::class.java, NumberConverter::class.java)
        this.with(UUID::class.java, UuidToStringConverter::class.java)
        this.with(Enum::class.java, EnumToStringConverter::class.java)
        this.with(OffsetDateTime::class.java, OffsetDateTimeToStringConverter::class.java)
    }

}
