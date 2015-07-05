package com.github.maxstupo.jannoargs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for setting fields.
 * @author Maxstupo
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AnnoArg {
	/** The key for referencing the field using command line arguments. */
	String key() default "key";
}
