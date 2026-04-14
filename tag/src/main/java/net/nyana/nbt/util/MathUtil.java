package net.nyana.nbt.util;

/**
 * 数学工具类，提供高性能的数值运算辅助方法。
 */
public final class MathUtil {

    private MathUtil() {}

    /**
     * 对浮点数执行快速向下取整（floor）运算。
     * <p>
     * 相较于 {@link Math#floor(double)}，此方法通过直接强转避免了装箱开销，性能更优。
     * 对于负数，强转会截断小数部分而非向下取整，因此需要额外处理。
     * </p>
     *
     * @param value 要取整的浮点数
     * @return 不大于 {@code value} 的最大整数
     */
    public static int fastFloor(double value) {
        int i = (int) value;
        return value < (double) i ? i - 1 : i;
    }
}
