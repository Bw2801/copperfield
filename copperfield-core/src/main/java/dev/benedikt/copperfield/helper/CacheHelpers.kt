package dev.benedikt.copperfield.helper

import com.google.common.cache.Cache

/**
 * Adds map-like methods for drop-in replacements.
 *
 * @author Benedikt Wüller
 */

/**
 * @see MutableMap.getOrPut
 */
fun <K, V> Cache<K, V>.getOrPut(key: K, compute: (K) -> V): V {
    val value = this.getIfPresent(key)
    if (value != null) return value

    val computedValue = compute(key)
    this[key] = computedValue
    return computedValue
}

/**
 * @see MutableMap.set
 */
operator fun <K, V> Cache<K, V>.set(key: K, value: V?) {
    if (value == null) {
        this.invalidate(key)
    } else {
        this.put(key, value)
    }
}
