package dev.benedikt.copperfield.helper

/**
 * Just some helpers to modify the case-ing of strings.
 *
 * @author Benedikt Wüller
 */

private val camelRegex = "(?<=[a-zA-Z])[A-Z]".toRegex()
private val snakeRegex = "_[a-zA-Z]".toRegex()

/**
 * Converts `camelCase` and `PascalCase` to `snake_case`.
 */
fun String.camelToSnakeCase() = camelRegex.replace(this) { "_${it.value}" }.toLowerCase()

/**
 * Converts `snake_case` to `camelCase`.
 */
fun String.snakeToCamelCase() = snakeRegex.replace(this) { it.value.replace("_", "").toUpperCase() }

/**
 * Converts `snake_case` to `PascalCase`.
 */
fun String.snakeToPascalCase() = this.snakeToCamelCase().capitalize()
