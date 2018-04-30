package pl.plajer.buildbattle.utils;

/*
 * Created by Tom on 30/07/2014.
 */

import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * <b>ReflectionUtils</b>
 * <p>
 * This class provides useful methods which makes dealing with reflection much easier, especially when working with Bukkit
 * <p>
 * You are welcome to use it, modify it and redistribute it under the following conditions:
 * <ul>
 * <li>Don't claim this class as your own
 * <li>Don't remove this disclaimer
 * </ul>
 *
 * <i>It would be nice if you provide credit to me if you use this class in a published project</i>
 *
 * @author DarkBlade12
 * @version 1.1
 */
public final class ReflectionHandler {
    // Prevent accidental construction
    private ReflectionHandler() {}

    /**
     * Returns the constructor of a given class with the given parameter types
     *
     * @param clazz          Target class
     * @param parameterTypes Parameter types of the desired constructor
     * @return The constructor of the target class with the specified parameter types
     * @throws NoSuchMethodException If the desired constructor with the specified parameter types cannot be found
     * @see DataType
     * @see DataType#getPrimitive(Class[])
     * @see DataType#compare(Class[], Class[])
     */
    public static Constructor<?> getConstructor(Class<?> clazz, Class<?>... parameterTypes) throws NoSuchMethodException {
        Class<?>[] primitiveTypes = DataType.getPrimitive(parameterTypes);
        for(Constructor<?> constructor : clazz.getConstructors()) {
            if(!DataType.compare(DataType.getPrimitive(constructor.getParameterTypes()), primitiveTypes)) {
                continue;
            }
            return constructor;
        }
        throw new NoSuchMethodException("There is no such constructor in this class with the specified parameter types");
    }

    /**
     * Returns a method of a class with the given parameter types
     *
     * @param clazz          Target class
     * @param methodName     Name of the desired method
     * @param parameterTypes Parameter types of the desired method
     * @return The method of the target class with the specified name and parameter types
     * @throws NoSuchMethodException If the desired method of the target class with the specified name and parameter types cannot be found
     * @see DataType#getPrimitive(Class[])
     * @see DataType#compare(Class[], Class[])
     */
    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        Class<?>[] primitiveTypes = DataType.getPrimitive(parameterTypes);
        for(Method method : clazz.getMethods()) {
            if(!method.getName().equals(methodName) || !DataType.compare(DataType.getPrimitive(method.getParameterTypes()), primitiveTypes)) {
                continue;
            }
            return method;
        }
        throw new NoSuchMethodException("There is no such method in this class with the specified name and parameter types");
    }

    /**
     * Returns a method of a desired class with the given parameter types
     *
     * @param className      Name of the desired target class
     * @param packageType    Package where the desired target class is located
     * @param methodName     Name of the desired method
     * @param parameterTypes Parameter types of the desired method
     * @return The method of the desired target class with the specified name and parameter types
     * @throws NoSuchMethodException  If the desired method of the desired target class with the specified name and parameter types cannot be found
     * @throws ClassNotFoundException If the desired target class with the specified name and package cannot be found
     *                                //* @see #getClass(String, PackageType)
     * @see #getMethod(Class, String, Class...)
     */
    public static Method getMethod(String className, PackageType packageType, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException, ClassNotFoundException {
        return getMethod(packageType.getClass(className), methodName, parameterTypes);
    }

    /**
     * Returns a field of the target class with the given name
     *
     * @param clazz     Target class
     * @param declared  Whether the desired field is declared or not
     * @param fieldName Name of the desired field
     * @return The field of the target class with the specified name
     * @throws NoSuchFieldException If the desired field of the given class cannot be found
     * @throws SecurityException    If the desired field cannot be made accessible
     */
    public static Field getField(Class<?> clazz, boolean declared, String fieldName) throws NoSuchFieldException, SecurityException {
        Field field = declared ? clazz.getDeclaredField(fieldName) : clazz.getField(fieldName);
        field.setAccessible(true);
        return field;
    }

    /**
     * Returns a field of a desired class with the given name
     *
     * @param className   Name of the desired target class
     * @param packageType Package where the desired target class is located
     * @param declared    Whether the desired field is declared or not
     * @param fieldName   Name of the desired field
     * @return The field of the desired target class with the specified name
     * @throws NoSuchFieldException   If the desired field of the desired class cannot be found
     * @throws SecurityException      If the desired field cannot be made accessible
     * @throws ClassNotFoundException If the desired target class with the specified name and package cannot be found
     * @see #getField(Class, boolean, String)
     */
    public static Field getField(String className, PackageType packageType, boolean declared, String fieldName) throws NoSuchFieldException, SecurityException, ClassNotFoundException {
        return getField(packageType.getClass(className), declared, fieldName);
    }

    /**
     * Sets the value of a field of the given class of an object
     *
     * @param instance  Target object
     * @param clazz     Target class
     * @param declared  Whether the desired field is declared or not
     * @param fieldName Name of the desired field
     * @param value     New value
     * @throws IllegalArgumentException If the type of the value does not match the type of the desired field
     * @throws IllegalAccessException   If the desired field cannot be accessed
     * @throws NoSuchFieldException     If the desired field of the target class cannot be found
     * @throws SecurityException        If the desired field cannot be made accessible
     * @see #getField(Class, boolean, String)
     */
    public static void setValue(Object instance, Class<?> clazz, boolean declared, String fieldName, Object value) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        getField(clazz, declared, fieldName).set(instance, value);
    }

    /**
     * Sets the value of a field with the given name of an object
     *
     * @param instance  Target object
     * @param declared  Whether the desired field is declared or not
     * @param fieldName Name of the desired field
     * @param value     New value
     * @throws IllegalArgumentException If the type of the value does not match the type of the desired field
     * @throws IllegalAccessException   If the desired field cannot be accessed
     * @throws NoSuchFieldException     If the desired field of the target object cannot be found
     * @throws SecurityException        If the desired field cannot be made accessible
     * @see #setValue(Object, Class, boolean, String, Object)
     */
    public static void setValue(Object instance, boolean declared, String fieldName, Object value) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
        setValue(instance, instance.getClass(), declared, fieldName, value);
    }

    /**
     * Represents an enumeration of dynamic packages of NMS and CraftBukkit
     * <p>
     * This class is part of the <b>ReflectionUtils</b> and follows the same usage conditions
     *
     * @author DarkBlade12
     * @since 1.0
     */
    public enum PackageType {
        MINECRAFT_SERVER("net.minecraft.server." + getServerVersion()), CRAFTBUKKIT("org.bukkit.craftbukkit." + getServerVersion()), CRAFTBUKKIT_BLOCK(CRAFTBUKKIT, "block"), CRAFTBUKKIT_CHUNKIO(CRAFTBUKKIT, "chunkio"), CRAFTBUKKIT_COMMAND(CRAFTBUKKIT, "command"), CRAFTBUKKIT_CONVERSATIONS(CRAFTBUKKIT, "conversations"), CRAFTBUKKIT_ENCHANTMENS(CRAFTBUKKIT, "enchantments"), CRAFTBUKKIT_ENTITY(CRAFTBUKKIT, "entity"), CRAFTBUKKIT_EVENT(CRAFTBUKKIT, "event"), CRAFTBUKKIT_GENERATOR(CRAFTBUKKIT, "generator"), CRAFTBUKKIT_HELP(CRAFTBUKKIT, "help"), CRAFTBUKKIT_INVENTORY(CRAFTBUKKIT, "inventory"), CRAFTBUKKIT_MAP(CRAFTBUKKIT, "map"), CRAFTBUKKIT_METADATA(CRAFTBUKKIT, "metadata"), CRAFTBUKKIT_POTION(CRAFTBUKKIT, "potion"), CRAFTBUKKIT_PROJECTILES(CRAFTBUKKIT, "projectiles"), CRAFTBUKKIT_SCHEDULER(CRAFTBUKKIT, "scheduler"), CRAFTBUKKIT_SCOREBOARD(CRAFTBUKKIT, "scoreboard"), CRAFTBUKKIT_UPDATER(CRAFTBUKKIT, "updater"), CRAFTBUKKIT_UTIL(CRAFTBUKKIT, "util");

        private final String path;

        /**
         * Construct a new package type
         *
         * @param path Path of the package
         */
        private PackageType(String path) {
            this.path = path;
        }

        /**
         * Construct a new package type
         *
         * @param parent Parent package of the package
         * @param path   Path of the package
         */
        private PackageType(PackageType parent, String path) {
            this(parent + "." + path);
        }

        /**
         * Returns the version of your server
         *
         * @return The server version
         */
        public static String getServerVersion() {
            return Bukkit.getServer().getClass().getPackage().getName().substring(23);
        }

        /**
         * Returns the class with the given name
         *
         * @param className Name of the desired class
         * @return The class with the specified name
         * @throws ClassNotFoundException If the desired class with the specified name and package cannot be found
         */
        public Class<?> getClass(String className) throws ClassNotFoundException {
            return Class.forName(this + "." + className);
        }

        // Override for convenience
        @Override
        public String toString() {
            return path;
        }
    }

    /**
     * Represents an enumeration of Java data types with corresponding classes
     * <p>
     * This class is part of the <b>ReflectionUtils</b> and follows the same usage conditions
     *
     * @author DarkBlade12
     * @since 1.0
     */
    public enum DataType {
        BYTE(byte.class, Byte.class), SHORT(short.class, Short.class), INTEGER(int.class, Integer.class), LONG(long.class, Long.class), CHARACTER(char.class, Character.class), FLOAT(float.class, Float.class), DOUBLE(double.class, Double.class), BOOLEAN(boolean.class, Boolean.class);

        private static final Map<Class<?>, DataType> CLASS_MAP = new HashMap<>();

        // Initialize map for quick class lookup
        static {
            for(DataType type : values()) {
                CLASS_MAP.put(type.primitive, type);
                CLASS_MAP.put(type.reference, type);
            }
        }

        private final Class<?> primitive;
        private final Class<?> reference;

        /**
         * Construct a new data type
         *
         * @param primitive Primitive class of this data type
         * @param reference Reference class of this data type
         */
        DataType(Class<?> primitive, Class<?> reference) {
            this.primitive = primitive;
            this.reference = reference;
        }

        /**
         * Returns the data type with the given primitive/reference class
         *
         * @param clazz Primitive/Reference class of the data type
         * @return The data type
         */
        public static DataType fromClass(Class<?> clazz) {
            return CLASS_MAP.get(clazz);
        }

        /**
         * Returns the primitive class of the data type with the given reference class
         *
         * @param clazz Reference class of the data type
         * @return The primitive class
         */
        public static Class<?> getPrimitive(Class<?> clazz) {
            DataType type = fromClass(clazz);
            return type == null ? clazz : type.getPrimitive();
        }

        /**
         * Returns the primitive class array of the given class array
         *
         * @param classes Given class array
         * @return The primitive class array
         */
        public static Class<?>[] getPrimitive(Class<?>[] classes) {
            int length = classes == null ? 0 : classes.length;
            Class<?>[] types = new Class<?>[length];
            for(int index = 0; index < length; index++) {
                types[index] = getPrimitive(classes[index]);
            }
            return types;
        }

        /**
         * Compares two class arrays on equivalence
         *
         * @param primary   Primary class array
         * @param secondary Class array which is compared to the primary array
         * @return Whether these arrays are equal or not
         */
        public static boolean compare(Class<?>[] primary, Class<?>[] secondary) {
            if(primary == null || secondary == null || primary.length != secondary.length) {
                return false;
            }
            for(int index = 0; index < primary.length; index++) {
                Class<?> primaryClass = primary[index];
                Class<?> secondaryClass = secondary[index];
                if(primaryClass.equals(secondaryClass) || primaryClass.isAssignableFrom(secondaryClass)) {
                    continue;
                }
                return false;
            }
            return true;
        }

        /**
         * Returns the primitive class of this data type
         *
         * @return The primitive class
         */
        public Class<?> getPrimitive() {
            return primitive;
        }
    }
}
