//  MIT License
//  
//  Copyright (c) 2020 fren_gor
//  
//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files (the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions:
//  
//  The above copyright notice and this permission notice shall be included in all
//  copies or substantial portions of the Software.
//  
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
//  SOFTWARE.

package com.fren_gor.packetInjectorAPI.tests;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.Nullable;

/**
 * Reflection class by fren_gor Give me credits if you use it in one of your plugin
 * 
 * @author fren_gor
 *
 */
public final class ReflectionUtil {

	private static final Map<String, Map<String, Field>> fields = new ConcurrentHashMap<>();

	/**
	 * Build a new class getting the proper constructor from parameters
	 * 
	 * @param clazz
	 *            The class of the object of witch you want the instance
	 * @param parameters
	 *            The contructors parameters
	 * @return The new instance
	 */
	public static Object newInstance(Class<?> clazz, Object... parameters) {

		Class<?>[] classes = new Class<?>[parameters.length];

		for (int i = 0; i < parameters.length; i++) {
			classes[i] = parameters[i].getClass();
		}

		try {
			Constructor<?> c = clazz.getDeclaredConstructor(classes);
			c.setAccessible(true);
			return c.newInstance(parameters);
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Invoke method in c class
	 * 
	 * @param object
	 *            The object where the method is invoked
	 * @param method
	 *            The name of the method
	 * @param parameters
	 *            The object uses as parameters
	 * @return What the method return. If the method is void, return null
	 */
	@Nullable
	public static Object invoke(Object object, String method, Object... parameters) {

		Method m = null;

		Class<?>[] classes = new Class<?>[parameters.length];

		for (int i = 0; i < parameters.length; i++) {
			classes[i] = parameters[i].getClass();
		}

		try {
			m = object.getClass().getDeclaredMethod(method, classes);
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
			return null;
		}
		Object o1;
		try {
			boolean b = m.isAccessible();
			m.setAccessible(true);
			o1 = m.invoke(object, parameters);
			m.setAccessible(b);
		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
			return null;
		}
		return o1;
	}

	/**
	 * Set a static field
	 * 
	 * @param object
	 *            The object where the field is set
	 * @param field
	 *            The name of the field
	 * @param newValue
	 *            The new value of the field
	 * @return If the execution was successful
	 */
	public static boolean setField(Class<?> clazz, String field, Object newValue) {
		return setField(null, clazz, field, newValue);
	}

	/**
	 * Set a field
	 * 
	 * @param object
	 *            The object where the field is set
	 * @param field
	 *            The name of the field
	 * @param newValue
	 *            The new value of the field
	 * @return If the execution was successful
	 */
	public static boolean setField(Object object, String field, Object newValue) {
		return setField(object, object.getClass(), field, newValue);
	}

	private static boolean setField(Object object, Class<?> c, String field, Object newValue) {

		if (fields.containsKey(c.getCanonicalName())) {
			Map<String, Field> fs = fields.get(c.getCanonicalName());
			if (fs.containsKey(field)) {
				try {
					fs.get(field).set(object, newValue);
				} catch (ReflectiveOperationException e) {
					return false;
				}
				return true;
			}
		}

		Class<?> current = c;
		Field f;
		while (true)
			try {
				f = current.getDeclaredField(field);
				break;
			} catch (ReflectiveOperationException e1) {
				current = current.getSuperclass();
				if (current != null) {
					continue;
				}
				return false;
			}

		f.setAccessible(true);

		Map<String, Field> map;
		if (fields.containsKey(c.getCanonicalName())) {
			map = fields.get(c.getCanonicalName());
		} else {
			map = new ConcurrentHashMap<>();
			fields.put(c.getCanonicalName(), map);
		}

		map.put(f.getName(), f);

		try {
			f.set(object, newValue);
		} catch (ReflectiveOperationException e) {
			return false;
		}

		return true;

	}

	/**
	 * Get a static field value
	 * 
	 * @param object
	 *            The object from which the represented field's value is to be extracted
	 * @param field
	 *            The field name
	 * @return The field value, null if the field doesn't exists.
	 */
	@Nullable
	public static Object getField(Class<?> clazz, String field) {
		return getField(null, clazz, field);
	}

	/**
	 * Get a field value
	 * 
	 * @param object
	 *            The object from which the represented field's value is to be extracted
	 * @param field
	 *            The field name
	 * @return The field value, null if the field doesn't exists.
	 */
	@Nullable
	public static Object getField(Object object, String field) {
		return getField(object, object.getClass(), field);
	}

	@Nullable
	private static Object getField(Object object, Class<?> c, String field) {

		if (fields.containsKey(c.getCanonicalName())) {
			Map<String, Field> fs = fields.get(c.getCanonicalName());
			if (fs.containsKey(field)) {
				try {
					return fs.get(field).get(object);
				} catch (ReflectiveOperationException e) {
					return null;
				}
			}
		}

		Class<?> current = c;
		Field f;
		while (true)
			try {
				f = current.getDeclaredField(field);
				break;
			} catch (ReflectiveOperationException e1) {
				current = current.getSuperclass();
				if (current != null) {
					continue;
				}
				return null;
			}

		f.setAccessible(true);

		Map<String, Field> map;
		if (fields.containsKey(c.getCanonicalName())) {
			map = fields.get(c.getCanonicalName());
		} else {
			map = new ConcurrentHashMap<>();
			fields.put(c.getCanonicalName(), map);
		}

		map.put(f.getName(), f);

		try {
			return f.get(object);
		} catch (ReflectiveOperationException e) {
			return null;
		}

	}

	/**
	 * Cast a object to a class
	 * 
	 * @param object
	 *            The object to cast
	 * @param clazz
	 *            The class
	 * @return The casted object
	 */
	@Nullable
	public static Object cast(Object object, Class<?> clazz) {
		try {
			return clazz.cast(object);
		} catch (ClassCastException e) {
			e.printStackTrace();
			return null;
		}
	}

}
