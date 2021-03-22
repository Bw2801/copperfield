package dev.benedikt.copperfield.proto.annotation

import com.google.protobuf.MessageLiteOrBuilder
import kotlin.reflect.KClass

/**
 * This annotation is required for any class implementing [dev.volix.rewinside.odyssey.common.copperfield.CopperConvertable] and being converted using
 * the proto context. It is used to determine which type of [com.google.protobuf.MessageLiteOrBuilder] to used when creating a new instance.
 *
 * @author Benedikt Wüller
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class CopperProtoClass(val type: KClass<out MessageLiteOrBuilder>)
