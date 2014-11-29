package com.github.maxstupo.jannoargs;

import java.lang.reflect.Field;

/**
 * JAnnoArgs class handles the reading of program arguments and sets fields with the annotation <code>AnnoArg</code>.
 * 
 * @author Maxstupo
 */
public class JAnnoArgs {

	/**
	 * Parse the args array, and set the fields denoted by <code>@AnnoArg()</code> in the given object.
	 * 
	 * @param obj
	 *            The object to set the fields.
	 * @param caseSensitive
	 *            If true parser will parse Annotations keys and argument keys without ignoring case.
	 * @param args
	 *            The arguments that will be parsed.
	 * @return false if an argument does not exist.
	 */
	public static boolean parse(Object obj, boolean caseSensitive, String... args) {
		Class<?> c = obj.getClass();

		String value = "";
		String key = "";
		for (int i = 0; i < args.length; i++) {
			String str = args[i].trim();

			if (isVarTag(str)) { // If string is a variable tag, ie has -- in front of it
				key = str.substring(2, str.length());
				value = "";
				continue;
			} else if (isBooleanTag(str)) { // If string is a boolean tag, ie has - in front of it
				String booleanKey = str.substring(1, str.length());

				JAnnoWrapper wrap = getAnnotationByKey(c, booleanKey, caseSensitive);
				if (wrap != null) {
					if (wrap.field.getType() == boolean.class) {
						set(obj, wrap.field, true);
					}
				} else {
					printInfo(c);
					return false;
				}
				continue;
			}

			value = value.isEmpty() ? str : (value + " " + str);

			JAnnoWrapper wrap = getAnnotationByKey(c, key, caseSensitive);
			if (wrap != null) {
				try {
					Object val = convert(wrap.field.getType(), value);
					set(obj, wrap.field, val);
				} catch (Exception e) {
					System.err.println("Invalid type, " + key + ": " + wrap.field.getType() + ", Value: " + value);
				}
			} else {
				printInfo(c);
				return false;
			}
		}
		return true;
	}

	private static void printInfo(Class<?> c) {
		System.out.println("Valid arguments: ");

		for (Field field : c.getDeclaredFields()) {
			AnnoArg anno = field.getAnnotation(AnnoArg.class);
			if (anno == null)
				continue;
			if (!anno.hidden())
				System.out.println("   " + anno.key() + " -  " + anno.desc());

		}
	}

	private static void set(Object theClass, Field field, Object obj) {
		boolean setAccessible = false;
		try {
			if (!field.isAccessible()) {
				field.setAccessible(true);
				setAccessible = true;
			}
			field.set(theClass, obj);
			if (setAccessible) {
				field.setAccessible(false);
			}

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	private static Object convert(Class<?> c, String val) throws Exception {
		if (c == String.class) {
			return val;
		} else if (c == int.class) {
			return Integer.parseInt(val);
		} else if (c == float.class) {
			return Float.parseFloat(val);
		} else if (c == double.class) {
			return Double.parseDouble(val);
		} else if (c == long.class) {
			return Long.parseLong(val);
		} else if (c == short.class) {
			return Short.parseShort(val);
		} else if (c == byte.class) {
			return Byte.parseByte(val);
		}
		return null;
	}
	
	/**
	 * Get AnnoArg annotation that has the key value equal to given key
	 * 
	 * @return The Wrapper containing the field and the annotation.
	 */
	private static JAnnoWrapper getAnnotationByKey(Class<?> classToSearch, String key, boolean caseSensitive) {
		for (Field field : classToSearch.getDeclaredFields()) {
			AnnoArg anno = field.getAnnotation(AnnoArg.class);
			if (anno == null)
				continue;

			String argKey = caseSensitive ? anno.key() : anno.key().toLowerCase();
			key = caseSensitive ? key : key.toLowerCase();
			if (argKey.equals(key)) {
				return new JAnnoWrapper(field, anno);
			}

		}
		return null;
	}

	private static boolean isBooleanTag(String str) {
		if (str.length() < 2)
			return false;
		return str.substring(0, 1).equalsIgnoreCase("-") && !str.substring(1, 2).equalsIgnoreCase("-");
	}

	private static boolean isVarTag(String str) {
		if (str.length() < 2)
			return false;
		return str.substring(0, 1).equalsIgnoreCase("-") && str.substring(1, 2).equalsIgnoreCase("-");
	}

}
