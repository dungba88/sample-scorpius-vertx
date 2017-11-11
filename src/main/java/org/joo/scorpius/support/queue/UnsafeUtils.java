package org.joo.scorpius.support.queue;
import java.lang.reflect.Field;

import sun.misc.Unsafe;

public class UnsafeUtils {

	private static final Unsafe UNSAFE;
	
	static { 
		try { 
			Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe"); 
			field.setAccessible(true);
			UNSAFE = (Unsafe) field.get(null); 
		} catch (Exception e) { 
			throw new AssertionError(e);
		}
	}
	
	public static void putObject(Object o, long offset, Object value) {
		UNSAFE.putObject(o, offset, value);
	}

	public static Object getObject(Object o, int offset) {
		return UNSAFE.getObject(o, offset);
	}

	public static int getInt(Object o, long offset) {
		return UNSAFE.getInt(o, offset);
	}

	public static long objectFieldOffset(Field declaredField) {
		return UNSAFE.objectFieldOffset(declaredField);
	}

	public static int arrayBaseOffset(Class<?> class1) {
		return UNSAFE.arrayBaseOffset(class1);
	}

	public static int arrayIndexScale(Class<?> class1) {
		return UNSAFE.arrayIndexScale(class1);
	}

	public static void putOrderedInt(Object o, long offset, int value) {
		UNSAFE.putOrderedInt(o, offset, value);
	}
}
