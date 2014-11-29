package com.github.maxstupo.jannoargs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for setting fields. The string "key" is the key when referencing the field using command line arguments.
 * 
 * @author Maxstupo
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AnnoArg {
	/** If true don't show the key or description in help. */
	boolean hidden() default false;

	/** The key for referencing the field using command line arguments. */
	String key() default "key";

	/** Return description about this AnnoArg annotation. */
	String desc() default "Unknown";
}
