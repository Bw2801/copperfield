package dev.benedikt.copperfield

/**
 * Indicates, that this and it's derived classes may contain convertable fields annotated with
 * [dev.volix.rewinside.odyssey.common.copperfield.annotation.CopperField] and can be converted by a
 * [dev.volix.rewinside.odyssey.common.copperfield.converter.CopperConvertableConverter]. It provides callback methods to be called before
 * and after conversion in both directions.
 *
 * All classes that implement this interface and are not abstract should always provide a default constructor without required parameters.
 *
 * @see dev.volix.rewinside.odyssey.common.copperfield.annotation.CopperField
 * @see dev.volix.rewinside.odyssey.common.copperfield.converter.CopperConvertableConverter
 *
 * @author Benedikt Wüller
 */
interface CopperConvertable {

    @JvmDefault
    fun onBeforeOursToTheirs() = Unit

    @JvmDefault
    fun onAfterOursToTheirs() = Unit

    @JvmDefault
    fun onBeforeTheirsToOurs() = Unit

    @JvmDefault
    fun onAfterTheirsToOurs() = Unit

}
