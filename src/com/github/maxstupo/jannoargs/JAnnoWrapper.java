package com.github.maxstupo.jannoargs;

import java.lang.reflect.Field;

/**
 * A wrapper class containing the Field and AnnoArg annotation.
 * 
 * @author Maxstupo
 */
public class JAnnoWrapper {
	public Field field;
	public AnnoArg anno;

	public JAnnoWrapper(Field field, AnnoArg anno) {
		this.field = field;
		this.anno = anno;
	}
}