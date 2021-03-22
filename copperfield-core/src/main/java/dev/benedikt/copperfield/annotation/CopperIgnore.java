package dev.benedikt.copperfield.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Flags the field to be ignored by the conversion process of CopperConvertableConverters.
 *
 * The {@link #contextTypes()} define all context types for which this field will be ignored. By default, this field will be ignored for all context
 * types.
 *
 * @author Benedikt Wüller
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CopperIgnore {
    Class[] contextTypes() default { Object.class };
}
