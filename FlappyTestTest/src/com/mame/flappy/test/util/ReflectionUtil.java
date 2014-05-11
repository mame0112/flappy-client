package com.mame.flappy.test.util;

import java.lang.reflect.Field;

public class ReflectionUtil {

	public static <T> Object getValue(Class<T> className, String fieldName,
			Object targetObject) {

		Object result = null;

		if (targetObject != null) {
			try {
				Field field = className.getDeclaredField(fieldName);
				field.setAccessible(true);
				result = field.get(targetObject);
				return result;
			} catch (Exception e) {
				return null;
			}
		}

		return null;
	}

	public static <T> void setFieldValue(Class<T> className,
			Object targetObject, String fieldName, Object targetValue) {

		if (targetObject != null) {
			try {
				Field field = className.getDeclaredField(fieldName);
				field.setAccessible(true);
				field.set(targetObject, targetValue);
			} catch (Exception e) {

			}
		}

	}
}
