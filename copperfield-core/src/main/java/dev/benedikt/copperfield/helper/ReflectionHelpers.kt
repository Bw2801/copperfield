package dev.benedikt.copperfield.helper

/**
 * @author Benedikt Wüller
 */

/**
 * Returns the annotation of the given [annotationType] declared on the given [type] or any of it's superclasses and implemented interfaces.
 * This basically reflects the functionality of [java.lang.annotation.Inherited] but also looks for interface annotations.
 */
fun <T : Annotation> getAnnotation(type: Class<*>, annotationType: Class<out T>): T? {
    var annotation = type.getDeclaredAnnotation(annotationType)
    if (annotation != null) return annotation

    if (type.superclass != null) {
        annotation = getAnnotation(type.superclass, annotationType)
        if (annotation != null) return annotation
    }

    return type.interfaces.mapNotNull { getAnnotation(it, annotationType) }.firstOrNull()
}
