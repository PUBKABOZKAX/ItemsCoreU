package me.udnek.itemscoreu.util;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sun.misc.Unsafe;

import java.lang.reflect.*;

public class Reflex {
    public static @NotNull Field getField(@NotNull Class<?> source, @NotNull String name) {
        try {
            return source.getDeclaredField(name);
        }
        catch (NoSuchFieldException exception) {
            Class<?> superClass = source.getSuperclass();
            Preconditions.checkArgument(superClass != null);
            return getField(superClass, name);
        }
    }

    public static @Nullable <T> T getFieldValue(@NotNull Object source, @NotNull String name) {
        Class<?> clazz = source instanceof Class<?> ? (Class<?>) source : source.getClass();
        Field field = getField(clazz, name);

        field.setAccessible(true);
        try {
            return (T) field.get(source);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }


    public static void setFieldValue(@NotNull Object source, @NotNull String name, @Nullable Object value) {
        try {
            boolean isStatic = source instanceof Class;
            Class<?> clazz = isStatic ? (Class<?>) source : source.getClass();

            Field field = getField(clazz, name);
            field.setAccessible(true);

/*
            if (Modifier.isFinal(field.getModifiers())) {
                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            }
*/


            field.set(isStatic ? null : source, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setRecordFieldValue(@NotNull Object instance, @NotNull String fieldName, @Nullable Object value) {
        try {
            Field f = instance.getClass().getDeclaredField(fieldName);

            Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            Unsafe unsafe = (Unsafe) unsafeField.get(null);

            Field theInternalUnsafeField = Unsafe.class.getDeclaredField("theInternalUnsafe");
            theInternalUnsafeField.setAccessible(true);
            Object theInternalUnsafe = theInternalUnsafeField.get(null);

            Method offset = Class.forName("jdk.internal.misc.Unsafe").getMethod("objectFieldOffset", Field.class);
            unsafe.putBoolean(offset, 12, true);

            unsafe.putObject(instance, (long) offset.invoke(theInternalUnsafe, f), value);
        } catch (IllegalAccessException | NoSuchFieldException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }


    public static @NotNull Method getMethod(@NotNull Class<?> clazz, @NotNull String name, @Nullable Class<?>... parameterTypes){
        try {
            Method method = clazz.getDeclaredMethod(name, parameterTypes);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
    public static @NotNull Method getMethod(@NotNull Class<?> clazz, @NotNull String name){
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals(name)){
                method.setAccessible(true);
                return method;
            }
        }
        throw new RuntimeException(new NoSuchMethodException(name));
    }

    public static @Nullable Object invokeMethod(@Nullable Object object, @NotNull Method method, @Nullable Object ...args){
        try {
            return method.invoke(object, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> @NotNull Constructor<T> getFirstConstructor(@NotNull Class<T> clazz){
        Constructor<?>[] constructor = clazz.getDeclaredConstructors();
        constructor[0].setAccessible(true);
        return (Constructor<T>) constructor[0];
    }

    public static <T> @NotNull Constructor<T> getConstructor(@NotNull Class<T> clazz, @NotNull Class<?> ...parameterTypes){
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor(parameterTypes);
            constructor.setAccessible(true);
            return constructor;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> @NotNull T construct(@NotNull Constructor<T> constructor, @Nullable Object ...args){
        try {
            return constructor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}

















