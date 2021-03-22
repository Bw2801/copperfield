package dev.benedikt.copperfield.registry

import dev.volix.rewinside.odyssey.common.copperfield.converter.CollectionConverter
import dev.volix.rewinside.odyssey.common.copperfield.converter.EnumToStringConverter
import dev.volix.rewinside.odyssey.common.copperfield.converter.MapConverter
import dev.volix.rewinside.odyssey.common.copperfield.converter.NumberConverter
import dev.volix.rewinside.odyssey.common.copperfield.converter.OffsetDateTimeToStringConverter
import dev.volix.rewinside.odyssey.common.copperfield.converter.UuidToStringConverter
import java.time.OffsetDateTime
import java.util.UUID

/**
 * The base registry contains the default converters always present in every [dev.volix.rewinside.odyssey.common.copperfield.CopperfieldAgent] by
 * default. It consists of the following [dev.volix.rewinside.odyssey.common.copperfield.converter.Converter]s:
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
