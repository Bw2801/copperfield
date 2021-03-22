package dev.benedikt.copperfield.converter

import dev.volix.rewinside.odyssey.common.copperfield.CopperfieldAgent
import dev.volix.rewinside.odyssey.common.copperfield.ValueAware
import dev.benedikt.copperfield.annotation.CopperCollectionType
import java.lang.reflect.Field
import java.util.ArrayList
import java.util.HashSet
import java.util.LinkedList
import java.util.Queue
import java.util.Stack

/**
 * Converts [Collection]s to a new [Iterable] with transformed values based on the optional
 * [dev.volix.rewinside.odyssey.common.copperfield.annotation.CopperValueType.type] annotation.
 *
 * @author Benedikt Wüller
 */
class CollectionConverter : Converter<Collection<*>, Collection<*>>(Collection::class.java, Collection::class.java), ValueAware {

    override fun toTheirs(value: Collection<*>?, agent: CopperfieldAgent, ourType: Class<out Collection<*>>, contextType: Class<out Any>, field: Field?): Collection<*>? {
        val valueType = this.getValueType(field)
        return value?.map { agent.toTheirs(it, valueType, contextType, field) }
    }

    override fun toOurs(value: Collection<*>?, agent: CopperfieldAgent, ourType: Class<out Collection<*>>, contextType: Class<out Any>, field: Field?): Collection<*>? {
        val valueType = this.getValueType(field)
        return this.convertToOurs(
            value?.map { agent.toOurs(it, valueType, contextType, field) },
            field, ourType
        )
    }

    private fun convertToOurs(value: Collection<*>?, field: Field?, type: Class<out Collection<*>>): Collection<*> {
        val iterableType = this.getIterableType(type, field)
        val instance = iterableType.newInstance() as MutableCollection<Any>
        if (value != null) {
            instance.addAll(value as Collection<Any>)
        }
        return instance
    }

    private fun getIterableType(type: Class<out Collection<*>>, field: Field?): Class<out Collection<Any>> {
        val annotation = field?.getDeclaredAnnotation(dev.benedikt.copperfield.annotation.CopperCollectionType::class.java)
        if (annotation != null) return annotation.type.java as Class<out Collection<Any>>

        return when {
            List::class.java.isAssignableFrom(type) -> ArrayList::class.java
            Set::class.java.isAssignableFrom(type) -> HashSet::class.java
            Queue::class.java.isAssignableFrom(type) -> LinkedList::class.java
            Stack::class.java.isAssignableFrom(type) -> Stack::class.java
            else -> ArrayList::class.java
        }
    }

}
