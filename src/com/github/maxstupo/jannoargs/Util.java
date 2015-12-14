package com.github.maxstupo.jannoargs;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author Maxstupo
 */
public final class Util {

	private static final Map<Class<?>, Class<?>> primitiveToWrapperMap = new HashMap<>();
	private static final Map<Class<?>, Class<?>> wrapperToPrimitiveMap = flipMap(primitiveToWrapperMap);

	private Util() {
	}

	static {
		primitiveToWrapperMap.put(boolean.class, Boolean.class);
		primitiveToWrapperMap.put(int.class, Integer.class);
		primitiveToWrapperMap.put(float.class, Float.class);
		primitiveToWrapperMap.put(double.class, Double.class);
		primitiveToWrapperMap.put(long.class, Long.class);
		primitiveToWrapperMap.put(short.class, Short.class);
		primitiveToWrapperMap.put(byte.class, Byte.class);
	}

	public static boolean isAssignable(Class<?> type1, Class<?> type2) {
		if (type1 == null || type2 == null)
			return false;

		if (type1.isAssignableFrom(type2))
			return true;
		if (type1.isPrimitive()) {
			Class<?> primitive = wrapperToPrimitiveMap.get(type2);
			return type1 == primitive;
		} else {
			Class<?> wrapper = primitiveToWrapperMap.get(type2);
			return wrapper != null && type1.isAssignableFrom(wrapper);
		}
	}

	private static <K, V> Map<V, K> flipMap(Map<K, V> map) {
		Map<V, K> flippedMap = new HashMap<>();
		for (Entry<K, V> entry : map.entrySet())
			flippedMap.put(entry.getValue(), entry.getKey());
		return flippedMap;
	}

	public static boolean isInt(String n) {
		try {
			Integer.parseInt(n);
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	public static boolean isFloat(String n) {
		try {
			Float.parseFloat(n);
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	public static boolean isDouble(String n) {
		try {
			Double.parseDouble(n);
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	public static boolean isLong(String n) {
		try {
			Long.parseLong(n);
			return true;
		} catch (Exception e) {
		}
		return false;
	}

}
