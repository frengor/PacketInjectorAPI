package com.fren_gor.packetUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;

public final class ReflectionUtil {

	@Nullable
	public static Object invoke(Object c, String method, Object... o) {

		Method m = null;

		Class<?>[] classes = new Class<?>[o.length];

		for (int i = 0; i < o.length; i++) {
			classes[i] = o[i].getClass();
		}

		try {
			m = c.getClass().getDeclaredMethod(method, classes);
		} catch (NoSuchMethodException | SecurityException e) {
			try {
				m = c.getClass().getSuperclass().getDeclaredMethod(method, classes);
			} catch (NoSuchMethodException | SecurityException ex) {
				try {
					m = c.getClass().getSuperclass().getSuperclass().getDeclaredMethod(method, classes);
				} catch (NoSuchMethodException | SecurityException exx) {

					exx.printStackTrace();

				}

			}
		}
		Object o1;
		try {
			boolean b = m.isAccessible();
			m.setAccessible(true);
			o1 = m.invoke(c, o);
			m.setAccessible(b);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
		return o1;
	}

	@Nullable
	public static Object setField(Object c, String field, Object o) {

		Field f;

		try {
			f = c.getClass().getDeclaredField(field);
		} catch (NoSuchFieldException | SecurityException e) {
			try {
				f = c.getClass().getSuperclass().getDeclaredField(field);
			} catch (SecurityException | NoSuchFieldException ex) {
				try {
					f = c.getClass().getSuperclass().getSuperclass().getDeclaredField(field);
				} catch (NoSuchFieldException | SecurityException exx) {
					exx.printStackTrace();
					return null;
				}

			}
		}

		try {
			boolean b = f.isAccessible();
			f.setAccessible(true);
			f.set(c, o);
			f.setAccessible(b);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}

		return c;

	}

	@Nullable
	public static Object getField(Object c, String field) {

		Field f;

		try {
			f = c.getClass().getDeclaredField(field);
		} catch (NoSuchFieldException | SecurityException e) {
			try {
				f = c.getClass().getSuperclass().getDeclaredField(field);
			} catch (SecurityException | NoSuchFieldException ex) {
				try {
					f = c.getClass().getSuperclass().getSuperclass().getDeclaredField(field);
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
			o = f.get(c);
			f.setAccessible(b);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}

		return o;

	}

	@Nullable
	public static Object cast(Object o, Class<?> c) {
		try {
			return c.cast(o);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

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
	
	public static String getVersion(){
		return Bukkit.getServer().getClass().getName().split("\\.")[3];
	}
	
	public static boolean versionIs1_7(){
		return Bukkit.getServer().getClass().getName().split("\\.")[3].startsWith("v1_7");
	}

}
