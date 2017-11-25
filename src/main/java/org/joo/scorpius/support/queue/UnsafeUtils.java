package org.joo.scorpius.support.queue;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class UnsafeUtils {

    private static final Unsafe UNSAFE;

    static {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            UNSAFE = (Unsafe) field.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void putObject(final Object o, final long offset, final Object value) {
        UNSAFE.putObject(o, offset, value);
    }

    public static Object getObject(final Object o, final long offset) {
        return UNSAFE.getObject(o, offset);
    }

    public static int getInt(final Object o, final long offset) {
        return UNSAFE.getInt(o, offset);
    }

    public static long objectFieldOffset(final Field declaredField) {
        return UNSAFE.objectFieldOffset(declaredField);
    }

    public static int arrayBaseOffset(final Class<?> class1) {
        return UNSAFE.arrayBaseOffset(class1);
    }

    public static int arrayIndexScale(final Class<?> class1) {
        return UNSAFE.arrayIndexScale(class1);
    }

    public static void putOrderedInt(final Object o, final long offset, final int value) {
        UNSAFE.putOrderedInt(o, offset, value);
    }
}
