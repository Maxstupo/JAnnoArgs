package com.github.maxstupo.jannoargs;

import java.lang.reflect.Field;

/**
 * A wrapper class containing the Field and AnnoArg annotation.
 * 
 * @author Maxstupo
 */
public class JAnnoWrapper {
	private final Field field;
	private final AnnoArg anno;

	public JAnnoWrapper(Field field, AnnoArg anno) {
		this.field = field;
		this.anno = anno;
	}

	public Field getField() {
		return field;
	}

	public AnnoArg getAnno() {
		return anno;
	}
}