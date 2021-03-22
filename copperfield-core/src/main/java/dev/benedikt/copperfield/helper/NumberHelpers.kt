package dev.benedikt.copperfield.helper

/**
 * @author Benedikt Wüller
 */

fun Number.convertToType(type: Class<out Number>): Number {
    return when(type) {
        Byte::class.javaPrimitiveType, Byte::class.javaObjectType -> this.toByte()
        Short::class.javaPrimitiveType, Short::class.javaObjectType -> this.toShort()
        Int::class.javaPrimitiveType, Int::class.javaObjectType -> this.toInt()
        Long::class.javaPrimitiveType, Long::class.javaObjectType -> this.toLong()
        Double::class.javaPrimitiveType, Double::class.javaObjectType -> this.toDouble()
        Float::class.javaPrimitiveType, Float::class.javaObjectType -> this.toFloat()
        else -> this
    }
}
