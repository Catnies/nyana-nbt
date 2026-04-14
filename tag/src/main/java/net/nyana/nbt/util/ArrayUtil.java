package net.nyana.nbt.util;

import java.lang.reflect.Array;

/**
 * 数组工具类，提供对基本类型数组的插入、删除和长度获取操作。
 */
public final class ArrayUtil {

    private ArrayUtil() {}

    /**
     * 在 {@code int} 数组的指定位置插入一个元素，返回新数组。
     *
     * @param array   原数组，可为 {@code null}（视为长度为 0 的数组）
     * @param index   插入位置，范围为 {@code [0, array.length]}
     * @param element 要插入的元素
     * @return 包含新元素的新数组
     * @throws IndexOutOfBoundsException 若 {@code index} 超出合法范围
     */
    public static int[] add(int[] array, int index, int element) {
        return (int[]) add(array, index, element, Integer.class);
    }

    /**
     * 在 {@code byte} 数组的指定位置插入一个元素，返回新数组。
     *
     * @param array   原数组，可为 {@code null}（视为长度为 0 的数组）
     * @param index   插入位置，范围为 {@code [0, array.length]}
     * @param element 要插入的元素
     * @return 包含新元素的新数组
     * @throws IndexOutOfBoundsException 若 {@code index} 超出合法范围
     */
    public static byte[] add(byte[] array, int index, byte element) {
        return (byte[]) add(array, index, element, Byte.class);
    }

    /**
     * 在 {@code long} 数组的指定位置插入一个元素，返回新数组。
     *
     * @param array   原数组，可为 {@code null}（视为长度为 0 的数组）
     * @param index   插入位置，范围为 {@code [0, array.length]}
     * @param element 要插入的元素
     * @return 包含新元素的新数组
     * @throws IndexOutOfBoundsException 若 {@code index} 超出合法范围
     */
    public static long[] add(long[] array, int index, long element) {
        return (long[]) add(array, index, element, Long.TYPE);
    }

    /**
     * 通用数组插入实现，供各基本类型重载方法调用。
     *
     * @param array   原数组，可为 {@code null}
     * @param index   插入位置
     * @param element 要插入的元素（已装箱）
     * @param clazz   数组元素的类型
     * @return 包含新元素的新数组对象
     * @throws IndexOutOfBoundsException 若 {@code index} 超出合法范围
     */
    private static Object add(Object array, int index, Object element, Class<?> clazz) {
        if (array == null) {
            if (index != 0) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Length: 0");
            } else {
                Object joinedArray = Array.newInstance(clazz, 1);
                Array.set(joinedArray, 0, element);
                return joinedArray;
            }
        } else {
            int length = Array.getLength(array);
            if (index <= length && index >= 0) {
                Object result = Array.newInstance(clazz, length + 1);
                System.arraycopy(array, 0, result, 0, index);
                Array.set(result, index, element);
                if (index < length) {
                    System.arraycopy(array, index, result, index + 1, length - index);
                }
                return result;
            } else {
                throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + length);
            }
        }
    }

    /**
     * 移除 {@code int} 数组中指定位置的元素，返回新数组。
     *
     * @param array 原数组，不可为 {@code null}
     * @param index 要移除的元素位置，范围为 {@code [0, array.length - 1]}
     * @return 移除指定元素后的新数组
     * @throws IndexOutOfBoundsException 若 {@code index} 超出合法范围
     */
    public static int[] remove(int[] array, int index) {
        return (int[]) remove((Object) array, index);
    }

    /**
     * 移除 {@code byte} 数组中指定位置的元素，返回新数组。
     *
     * @param array 原数组，不可为 {@code null}
     * @param index 要移除的元素位置，范围为 {@code [0, array.length - 1]}
     * @return 移除指定元素后的新数组
     * @throws IndexOutOfBoundsException 若 {@code index} 超出合法范围
     */
    public static byte[] remove(byte[] array, int index) {
        return (byte[]) remove((Object) array, index);
    }

    /**
     * 移除 {@code long} 数组中指定位置的元素，返回新数组。
     *
     * @param array 原数组，不可为 {@code null}
     * @param index 要移除的元素位置，范围为 {@code [0, array.length - 1]}
     * @return 移除指定元素后的新数组
     * @throws IndexOutOfBoundsException 若 {@code index} 超出合法范围
     */
    public static long[] remove(long[] array, int index) {
        return (long[]) remove((Object) array, index);
    }

    /**
     * 通用数组移除实现，供各基本类型重载方法调用。
     *
     * @param array 原数组
     * @param index 要移除的元素位置
     * @return 移除指定元素后的新数组对象
     * @throws IndexOutOfBoundsException 若 {@code index} 超出合法范围
     */
    private static Object remove(Object array, int index) {
        int length = getLength(array);
        if (index >= 0 && index < length) {
            Object result = Array.newInstance(array.getClass().getComponentType(), length - 1);
            System.arraycopy(array, 0, result, 0, index);
            if (index < length - 1) {
                System.arraycopy(array, index + 1, result, index, length - index - 1);
            }
            return result;
        } else {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + length);
        }
    }

    /**
     * 获取数组的长度，若数组为 {@code null} 则返回 {@code 0}。
     *
     * @param array 目标数组，可为 {@code null}
     * @return 数组长度，或 {@code 0}（当 {@code array} 为 {@code null} 时）
     */
    public static int getLength(Object array) {
        return array == null ? 0 : Array.getLength(array);
    }
}
