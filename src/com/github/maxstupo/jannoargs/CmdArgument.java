package com.github.maxstupo.jannoargs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used by JAnnoArgs for setting a field from command line arguments.
 * 
 * @author Maxstupo
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CmdArgument {
	/**
	 * The key for referencing the field using command line arguments.
	 * 
	 * @return the key for referencing the field using command line arguments.
	 */
	String key();

	/**
	 * The description of what this argument does. (Default: "")
	 * 
	 * @return the description of what this argument does.
	 */
	String desc() default "";

	/**
	 * If true this argument will not be listed in the help. (Default: false)
	 * 
	 * @return true if this arument will not be listed in the help.
	 */
	boolean hide() default false;
}
