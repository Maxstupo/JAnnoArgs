package com.github.maxstupo.jannoargs;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * JAnnoArgs class handles the reading of program arguments and sets fields with the annotation <code>AnnoArg</code>.
 * 
 * @author Maxstupo
 */
public class JAnnoArgs {
	private static JAnnoArgs instance;

	private JAnnoArgs() {
	}

	/**
	 * Parse given string array, and set the fields that are tagged with a {@link CmdArgument} annotation from the given object.<br>
	 * Supported data types are<code> Boolean, String, Integer, Float, Double, Long </code> and the listed primitive counterparts.
	 * 
	 * @param obj The object to look for fields and apply values to fields.
	 * @param args
	 */
	public void parseArguments(Object obj, String... args) {
		Map<String, Field> keyToFields = createKeyToFieldsMap(obj);

		boolean assignNext = false;
		String key = null;
		for (String arg : args) {
			if (assignNext) {
				handleVariable(keyToFields, obj, key, arg);
				assignNext = false;
			}

			if (arg.startsWith("--")) {
				key = arg.substring(2);
				assignNext = true;
			} else if (arg.startsWith("-")) {
				handleBoolean(keyToFields, obj, arg.substring(1), false);
			} else if (arg.startsWith("+")) {
				handleBoolean(keyToFields, obj, arg.substring(1), true);
			}

		}
	}

	private boolean handleBoolean(Map<String, Field> keyToFields, Object obj, String key, boolean state) {
		Field field = keyToFields.get(key);
		if (field != null && Util.isAssignable(field.getType(), boolean.class)) {
			try {
				field.setAccessible(true);
				field.set(obj, state);
				return true;
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private boolean handleVariable(Map<String, Field> keyToFields, Object obj, String key, String value) {
		Field field = keyToFields.get(key);
		if (field != null) {
			Object objValue = convertStringToType(value, field.getType());
			if (objValue == null)
				return false;
			try {
				field.setAccessible(true);
				field.set(obj, objValue);
				return true;
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private Object convertStringToType(String value, Class<?> type) {
		if (Util.isAssignable(type, String.class))
			return value;

		if (Util.isAssignable(type, int.class) && Util.isInt(value))
			return Integer.parseInt(value);

		if (Util.isAssignable(type, float.class) && Util.isFloat(value))
			return Float.parseFloat(value);

		if (Util.isAssignable(type, double.class) && Util.isDouble(value))
			return Double.parseDouble(value);

		if (Util.isAssignable(type, long.class) && Util.isLong(value))
			return Long.parseLong(value);

		return null;
	}

	public Map<String, Field> createKeyToFieldsMap(Object obj) {
		Map<String, Field> keyToField = new HashMap<String, Field>();

		for (Field field : obj.getClass().getDeclaredFields()) {
			CmdArgument anno = field.getAnnotation(CmdArgument.class);
			if (anno == null)
				continue;
			keyToField.put(anno.key(), field);
		}

		return keyToField;
	}

	public static final JAnnoArgs get() {
		if (instance == null)
			instance = new JAnnoArgs();
		return instance;
	}

}
