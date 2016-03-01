package com.github.maxstupo.jannoargs;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * JAnnoArgs class handles the reading of program arguments and sets fields with the annotation {@link CmdArgument}.
 * 
 * @author Maxstupo
 */
public class JAnnoArgs {
	private static JAnnoArgs instance;

	private final Map<String, IArgumentEvent> events = new HashMap<>();

	private JAnnoArgs() {
	}

	public boolean registerEvent(String argumentName, IArgumentEvent evt) {

		if (events.containsKey(argumentName))
			return false;

		events.put(argumentName, evt);
		return true;
	}

	/**
	 * Parse given string array, and set the fields that are annotated with a {@link CmdArgument} annotation from the given object. Supported data types are<code> Boolean, String, Integer, Float, Double, Long </code> and the listed primitive counterparts.
	 * <p>
	 * 
	 * Syntax:<br>
	 * - Boolean fields prefix the key with plus(+) for true, or prefix a hyphen(-) for false.<br>
	 * - Value fields prefix the key with double hyphens (--) followed by a space and the value.
	 * 
	 * @param obj
	 *            The object to look for fields and apply values to fields.
	 * @param args
	 */
	public void parseArguments(Object obj, String... args) {
		Map<String, Field> keyToFields = createKeyToFieldsMap(obj);
		List<String> keys = new ArrayList<>();

		boolean assignNext = false;
		String key = null;
		for (String arg : args) {
			if (assignNext) {
				handleVariable(keyToFields, obj, key, arg);
				keys.add(key);
				assignNext = false;
			}

			if (arg.startsWith("--")) {
				key = arg.substring(2);
				assignNext = true;
			} else if (arg.startsWith("-")) {
				String tempKey = arg.substring(1);
				handleBoolean(keyToFields, obj, tempKey, false);
				keys.add(tempKey);

			} else if (arg.startsWith("+")) {
				String tempKey = arg.substring(1);
				handleBoolean(keyToFields, obj, tempKey, true);
				keys.add(tempKey);
			}

		}

		
		for (Entry<String, IArgumentEvent> entry : events.entrySet()) {
			
			if (!keys.contains(entry.getKey()))
				continue;
			entry.getValue().onEvent(entry.getKey());
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

	/**
	 * Gets all fields annotated with {@link CmdArgument} from the given object and maps them to the given {@link CmdArgument#key() key} from the annotation.
	 * 
	 * @param obj
	 *            The object to search for fields.
	 * @return A map containing {@link Field fields} referenced by {@link CmdArgument#key() keys}.
	 */
	public static Map<String, Field> createKeyToFieldsMap(Object obj) {
		Map<String, Field> keyToField = new HashMap<String, Field>();

		for (Field field : obj.getClass().getDeclaredFields()) {
			CmdArgument anno = field.getAnnotation(CmdArgument.class);
			if (anno == null)
				continue;
			keyToField.put(anno.key(), field);
		}

		return keyToField;
	}

	/**
	 * Prints all fields in the given object annotated with a {@link CmdArgument} annotation with format of [{@link CmdArgument#key() key}: (fieldName = fieldValue)]
	 * 
	 * @param obj
	 *            The object to look for fields.
	 */
	public static void printCmdArgumentFields(Object obj) {
		Map<String, Field> keyToFields = createKeyToFieldsMap(obj);

		for (Entry<String, Field> entry : keyToFields.entrySet()) {
			try {
				Field field = entry.getValue();
				field.setAccessible(true);

				System.out.println(entry.getKey() + ": ('" + entry.getValue().getName() + "' = '" + field.get(obj) + "')");

			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}

	public static final JAnnoArgs get() {
		if (instance == null)
			instance = new JAnnoArgs();
		return instance;
	}

}
