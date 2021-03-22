package dev.benedikt.copperfield.exception

import java.lang.reflect.Field

/**
 * @author Benedikt Wüller
 */
open class CopperFieldException : RuntimeException {

    constructor(message: String) : super(message)

    constructor(message: String, throwable: Throwable) : super(message, throwable)

    constructor(throwable: Throwable) : super(throwable)

    constructor(field: Field?, contextType: Class<out Any>, message: String?) : super(createMessage(field, contextType, message))

    constructor(field: Field?, contextType: Class<out Any>, message: String?, cause: Throwable?) : super(createMessage(field, contextType, message), cause)

    constructor(field: Field?, contextType: Class<out Any>, cause: Throwable?) : super(createMessage(field, contextType), cause)

}

private fun createMessage(field: Field?, contextType: Class<out Any>, message: String? = null): String {
    val suffix = if (field != null) {
        "Field ${field.name} on class ${field.declaringClass.name} with contextType ${contextType.name}"
    } else {
        "ContextType ${contextType.name}"
    }

    return if (message == null) suffix else "$message | $suffix"
}
