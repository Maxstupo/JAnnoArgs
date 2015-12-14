package com.github.maxstupo.jannoargs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used by JAnnoArgs for setting a field from command line arguments.
 * @author Maxstupo
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CmdArgument {
	/** The key for referencing the field using command line arguments. */
	String key();
	
}
