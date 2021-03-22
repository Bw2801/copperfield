package dev.benedikt.copperfield.converter

import com.google.common.cache.CacheBuilder
import dev.benedikt.copperfield.CopperConvertable
import dev.benedikt.copperfield.CopperFieldDefinition
import dev.benedikt.copperfield.CopperTypeMapper
import dev.benedikt.copperfield.CopperfieldAgent
import dev.benedikt.copperfield.annotation.CopperField
import dev.benedikt.copperfield.annotation.CopperFields
import dev.benedikt.copperfield.exception.CopperFieldException
import dev.benedikt.copperfield.helper.camelToSnakeCase
import dev.benedikt.copperfield.helper.getAnnotation
import dev.benedikt.copperfield.helper.getOrPut
import java.lang.reflect.Field
import java.util.concurrent.TimeUnit

/**
 * Converts [CopperConvertable] instances to instances assignable from [T].
 *
 * This is achieved by parsing the class for [CopperField]s and applying the defined or fallback [Converter]s and [CopperTypeMapper]s to each value
 * before/after setting those to the target instances respectively.
 *
 * @author Benedikt Wüller
 */
abstract class CopperConvertableConverter<T : Any>(theirType: Class<T>) : Converter<CopperConvertable, T>(CopperConvertable::class.java, theirType) {

    /**
     * Caches field definitions by class types.
     */
    private val fieldDefinitions = CacheBuilder.newBuilder()
        .expireAfterAccess(5, TimeUnit.MINUTES)
        .build<Class<out Any>, List<CopperFieldDefinition>>()

    override fun toTheirs(value: CopperConvertable?, agent: CopperfieldAgent, ourType: Class<out CopperConvertable>, contextType: Class<out Any>, field: Field?): T? {
        val instance = this.createTheirInstance(contextType, value?.javaClass)

        // If the value is null, there are no field we can get the values of.
        if (value != null) {
            value.onBeforeOursToTheirs()
            this.getCopperFields(ourType, this.getMappedContextType(ourType, contextType)).forEach {
                try {
                    val fieldValue = it.field.get(value)
                    val fieldType = fieldValue?.javaClass ?: it.field.type

                    @Suppress("UNCHECKED_CAST")
                    val typeMapper = it.typeMapper as CopperTypeMapper<CopperConvertable, CopperConvertable>?

                    val mappedType = typeMapper?.mapType(value, fieldType) ?: fieldType
                    val converter = agent.findConverter(mappedType, it.converter, contextType)
                    val convertedValue = if (converter == null) fieldValue else agent.toTheirsWithConverter(fieldValue, mappedType, converter, contextType, it.field)

                    val writeType = when {
                        converter == null -> convertedValue?.javaClass ?: mappedType
                        Map::class.java.isAssignableFrom(converter.theirType) -> Map::class.java
                        Iterable::class.java.isAssignableFrom(converter.theirType) -> Iterable::class.java
                        else -> convertedValue?.javaClass ?: converter.theirType
                    }

                    this.setValue(instance, it.name, convertedValue, this.getNonPrimitiveType(writeType))
                } catch (ex: Exception) {
                    throw CopperFieldException(it.field, contextType, "An error occurred while converting toTheirs.", ex)
                }
            }
            value.onAfterOursToTheirs()
        }

        return this.finalizeTheirInstance(instance)
    }

    override fun toOurs(value: T?, agent: CopperfieldAgent, ourType: Class<out CopperConvertable>, contextType: Class<out Any>, field: Field?): CopperConvertable? {
        val instance = this.createOurInstance(ourType)
        instance.onBeforeTheirsToOurs()

        // If the value is null, we keep the default values so there is nothing to do here.
        if (value != null) {
            this.getCopperFields(ourType, this.getMappedContextType(ourType, contextType)).forEach {
                try {
                    val fieldType = it.field.type

                    @Suppress("UNCHECKED_CAST")
                    val typeMapper = it.typeMapper as CopperTypeMapper<CopperConvertable, CopperConvertable>?

                    val mappedType = typeMapper?.mapType(instance, fieldType) ?: fieldType
                    val converter = agent.findConverter(mappedType, it.converter, contextType)

                    val readType = when {
                        converter == null -> mappedType
                        Map::class.java.isAssignableFrom(converter.theirType) -> Map::class.java
                        Iterable::class.java.isAssignableFrom(converter.theirType) -> Iterable::class.java
                        else -> converter.theirType
                    }

                    val fieldValue = this.getValue(value, it.name, this.getNonPrimitiveType(readType))
                    val convertedValue = (if (converter == null) {
                        fieldValue
                    } else {
                        agent.toOursWithConverter(fieldValue, mappedType, converter, contextType, it.field)
                    }) ?: return@forEach

                    it.field.set(instance, convertedValue)
                } catch (ex: Exception) {
                    throw CopperFieldException(it.field, contextType, "An error occurred while converting toOurs.", ex)
                }
            }
        }

        val finalInstance = this.finalizeOurInstance(instance)
        finalInstance.onAfterTheirsToOurs()
        return finalInstance
    }

    /**
     * Returns all [CopperFieldDefinition] for this [type] and all it's supertypes which are not ignored for the context of [theirType].
     */
    private fun <T : Any> getCopperFields(type: Class<out T>, theirType: Class<out Any>): List<CopperFieldDefinition> {
        return this.fieldDefinitions.getOrPut(type) {
            // Find all declared fields including those of the supertypes.
            this.getDeclaredFields(type)
                .map { // Load the annotation and convert the field to a [CopperFieldDefinition.
                    it.isAccessible = true

                    val annotation = it.getDeclaredAnnotation(CopperField::class.java)
                    val name = if (annotation?.name?.isEmpty() != false) it.name.camelToSnakeCase() else annotation.name
                    val converterType = this.getConverterType(annotation?.converter?.java ?: Converter::class.java, it)

                    val typeMapperType = this.getTypeMapper(annotation?.typeMapper?.java ?: CopperTypeMapper::class.java, it)
                    val typeMapper = if (typeMapperType == CopperTypeMapper::class.java) null else typeMapperType.newInstance()

                    return@map CopperFieldDefinition(
                        it,
                        this.getName(name, it),
                        converterType,
                        typeMapper
                    )
                }
                .sortedWith(Comparator { o1, o2 -> // Sort the field dependency on each other.
                    // First handle all fields with no requirements.
                    if (o1.typeMapper == null && o2.typeMapper != null) {
                        return@Comparator -1
                    } else if (o1.typeMapper != null && o2.typeMapper == null) {
                        return@Comparator 1
                    } else if (o1.typeMapper == null && o2.typeMapper == null) {
                        return@Comparator 0
                    }

                    // Next, sort by requirements.
                    if (o1.typeMapper!!.requires(o2.field)) {
                        return@Comparator 1
                    } else if (o2.typeMapper!!.requires(o1.field)) {
                        return@Comparator -1
                    }

                    return@Comparator 0
                })
        }.filterNot { definition -> // Make sure the field is not ignored for [theirType].
            val annotation = definition.field.getAnnotation(dev.benedikt.copperfield.annotation.CopperIgnore::class.java) ?: return@filterNot false
            return@filterNot annotation.contextTypes.any { it.java.isAssignableFrom(theirType) }
        }
    }

    /**
     * Returns all declared fields for this [type] and all it's supertypes where either the [CopperField] or [CopperFields] annotation is present.
     */
    private fun getDeclaredFields(type: Class<*>): Collection<Field> {
        val fields = mutableListOf<Field>()
        var currentType: Class<*>? = type
        do {
            val declaresAllFields = getAnnotation(currentType!!, CopperFields::class.java) != null

            currentType.declaredFields
                .filter { declaresAllFields || it.isAnnotationPresent(CopperField::class.java) }
                .forEach { fields.add(it) }

            currentType = currentType.superclass
        } while (currentType != null)
        return fields
    }

    /**
     * Converts the given [type] to it's wrapper form if it is primitive.
     */
    private fun getNonPrimitiveType(type: Class<*>): Class<*> {
        return when (type) {
            Byte::class.javaPrimitiveType -> Byte::class.javaObjectType
            Short::class.javaPrimitiveType -> Short::class.javaObjectType
            Int::class.javaPrimitiveType -> Int::class.javaObjectType
            Long::class.javaPrimitiveType -> Long::class.javaObjectType
            Float::class.javaPrimitiveType -> Float::class.javaObjectType
            Double::class.javaPrimitiveType -> Double::class.javaObjectType
            Number::class.javaPrimitiveType -> Number::class.javaObjectType
            else -> type
        }
    }

    /**
     * Creates a new instance of [type].
     */
    protected open fun <T : CopperConvertable> createOurInstance(type: Class<T>): T = type.newInstance()

    /**
     * Performs finalization on the instance created by [createOurInstance] and returns either the same or a new instance afterwards.
     */
    protected open fun finalizeOurInstance(instance: CopperConvertable) = instance

    /**
     * Creates a new instance of [type] based on [ourType].
     */
    protected abstract fun createTheirInstance(type: Class<out Any>, ourType: Class<out CopperConvertable>?): T

    /**
     * Performs finalization on the instance created by [createTheirInstance] and returns either the same or a new instance afterwards.
     */
    protected open fun finalizeTheirInstance(instance: T) = instance

    /**
     * Reads the value of field [name] of the given [type] from the given [instance].
     */
    protected abstract fun getValue(instance: T, name: String, type: Class<*>): Any?

    /**
     * Writes the given [value] of the given [type] to the given field [name] on the given [instance].
     */
    protected abstract fun setValue(instance: T, name: String, value: Any?, type: Class<*>)

    /**
     * Returns the final name based on the default [name] of the given [field].
     */
    protected open fun getName(name: String, field: Field): String = name

    /**
     * Returns the final [Converter] type based on the default [type] of the given [field].
     */
    protected open fun getConverterType(type: Class<out Converter<out Any, out Any>>, field: Field): Class<out Converter<out Any, out Any>> = type

    /**
     * Returns the final [CopperTypeMapper] type based on the default [type] of the given [field].
     */
    protected open fun getTypeMapper(type: Class<out CopperTypeMapper<out CopperConvertable, out CopperConvertable>>, field: Field): Class<out CopperTypeMapper<out CopperConvertable, out CopperConvertable>> = type

    /**
     * Maps and returns a concrete type based on the instance [type] and the current [contextType].
     */
    protected open fun getMappedContextType(type: Class<out CopperConvertable>, contextType: Class<out Any>): Class<out Any> = contextType

}
