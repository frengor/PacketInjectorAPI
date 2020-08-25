package com.fren_gor.packetUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;

import lombok.Getter;

/**
 * Reflection class by fren_gor Give me credits if you use it in one of your plugin
 * 
 * @author fren_gor
 *
 */
public final class ReflectionUtil {

	private static Map<String, Map<String, Field>> fields = new ConcurrentHashMap<>();
	
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
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
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
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
		Object o1;
		try {
			boolean b = m.isAccessible();
			m.setAccessible(true);
			o1 = m.invoke(object, parameters);
			m.setAccessible(b);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
		return o1;
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
	@Nullable
	public static boolean setField(Object object, String field, Object newValue) {

		Class<?> c = object.getClass();

		if (fields.containsKey(c.getCanonicalName())) {
			Map<String, Field> fs = fields.get(c.getCanonicalName());
			if (fs.containsKey(field)) {
				try {
					fs.get(field).set(object, newValue);
				} catch (IllegalArgumentException | IllegalAccessException e) {
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
			} catch (NoSuchFieldException | SecurityException e1) {
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
		} catch (IllegalArgumentException | IllegalAccessException e) {
			return false;
		}

		return true;

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

		Class<?> c = object.getClass();

		if (fields.containsKey(c.getCanonicalName())) {
			Map<String, Field> fs = fields.get(c.getCanonicalName());
			if (fs.containsKey(field)) {
				try {
					return fs.get(field).get(object);
				} catch (IllegalArgumentException | IllegalAccessException e) {
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
			} catch (NoSuchFieldException | SecurityException e1) {
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
		} catch (IllegalArgumentException | IllegalAccessException e) {
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
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @author michel_0
	 * @param name
	 *            The class name
	 * @return The NMS class
	 */
	public static Class<?> getNMSClass(String name) {
		try {
			return Class.forName(
					"net.minecraft.server." + Bukkit.getServer().getClass().getName().split("\\.")[3] + "." + name);
		} catch (ClassNotFoundException e) {
			Bukkit.getLogger().info("[Reflection] Can't find NMS Class! (" + "net.minecraft.server."
					+ Bukkit.getServer().getClass().getName().split("\\.")[3] + "." + name + ")");
			return null;
		}
	}

	/**
	 * @author michel_0
	 * @param name
	 *            The class name
	 * @return The CraftBukkit class
	 */
	public static Class<?> getCBClass(String name) {
		try {
			return Class.forName(
					"org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getName().split("\\.")[3] + "." + name);
		} catch (ClassNotFoundException e) {
			Bukkit.getLogger().info("[Reflection] Can't find CB Class! (" + "org.bukkit.craftbukkit."
					+ Bukkit.getServer().getClass().getName().split("\\.")[3] + "." + name + ")");
			return null;
		}
	}

	/**
	 * Get the server version
	 * 
	 * @return The server version
	 */
	public static String getCompleteVersion() {
		return Bukkit.getServer().getClass().getName().split("\\.")[3];
	}

	@Getter
	private static int version = Integer.valueOf(getCompleteVersion().split("_")[1]);
	@Getter
	private static int release = Integer.valueOf(getCompleteVersion().split("R")[1]);

	/**
	 * Check if the server is in 1.7
	 * 
	 * @return If the server is in 1.7
	 */
	public static boolean versionIs1_7() {
		return version == 7;
	}

	/**
	 * Check if the server is in 1.14+
	 * 
	 * @return If the server is in 1.14+
	 */
	public static boolean versionIsAtLeast1_14() {
		return version >= 14;
	}

}
