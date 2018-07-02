package com.fren_gor.packetUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;

/**
 * Reflection class by fren_gor
 * Give me credits if you use it in one of your plugin
 * 
 * @author fren_gor
 *
 */
public final class ReflectionUtil {

	/**
	 * Invoke method in c class
	 * @param object The object where the method is invoked
	 * @param method The name of the method
	 * @param parameters The object uses as parameters
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
			try {
				m = object.getClass().getSuperclass().getDeclaredMethod(method, classes);
			} catch (NoSuchMethodException | SecurityException ex) {
				try {
					m = object.getClass().getSuperclass().getSuperclass().getDeclaredMethod(method, classes);
				} catch (NoSuchMethodException | SecurityException exx) {

					exx.printStackTrace();

				}

			}
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
	 * @param object The object where the field is set
	 * @param field The name of the field
	 * @param newValue The new value of the field
	 * @return The object
	 */
	@Nullable
	public static Object setField(Object object, String field, Object newValue) {

		Field f;

		try {
			f = object.getClass().getDeclaredField(field);
		} catch (NoSuchFieldException | SecurityException e) {
			try {
				f = object.getClass().getSuperclass().getDeclaredField(field);
			} catch (SecurityException | NoSuchFieldException ex) {
				try {
					f = object.getClass().getSuperclass().getSuperclass().getDeclaredField(field);
				} catch (NoSuchFieldException | SecurityException exx) {
					exx.printStackTrace();
					return null;
				}

			}
		}

		try {
			boolean b = f.isAccessible();
			f.setAccessible(true);
			f.set(object, newValue);
			f.setAccessible(b);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}

		return object;

	}

	/**
	 * Get a field value
	 * @param object The object from which the represented field's value is to be extracted
	 * @param field The field name
	 * @return The field value
	 */
	@Nullable
	public static Object getField(Object object, String field) {

		Field f;

		try {
			f = object.getClass().getDeclaredField(field);
		} catch (NoSuchFieldException | SecurityException e) {
			try {
				f = object.getClass().getSuperclass().getDeclaredField(field);
			} catch (SecurityException | NoSuchFieldException ex) {
				try {
					f = object.getClass().getSuperclass().getSuperclass().getDeclaredField(field);
				} catch (NoSuchFieldException | SecurityException exx) {
					exx.printStackTrace();
					return null;
				}
			}
		}
		Object o = null;
		try {
			boolean b = f.isAccessible();
			f.setAccessible(true);
			o = f.get(object);
			f.setAccessible(b);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}

		return o;

	}

	/**
	 * Cast a object to a class
	 * @param object The object to cast
	 * @param clazz The class
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
	 * @param name The class name
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
	 * @param name The class name
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
	 * @return The server version
	 */
	public static String getVersion(){
		return Bukkit.getServer().getClass().getName().split("\\.")[3];
	}
	
	/**
	 * Check if the server is in 1.7
	 * @return If the server is in 1.7
	 */
	public static boolean versionIs1_7(){
		return Bukkit.getServer().getClass().getName().split("\\.")[3].startsWith("v1_7");
	}

}
