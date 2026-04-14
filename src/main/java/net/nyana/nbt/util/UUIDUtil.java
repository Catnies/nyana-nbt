package net.nyana.nbt.util;

import java.util.UUID;

/**
 * UUID 工具类，提供 {@link UUID} 与 int[] 之间的互相转换.
 */
public class UUIDUtil {

    private UUIDUtil() {}

    /**
     * 将长度为 4 的 int[] 转换为 {@link UUID}.
     * <p>
     * 数组布局：{@code [mostHigh, mostLow, leastHigh, leastLow]},
     * 其中前两个元素构成 most significant bits，后两个元素构成 least significant bits.
     * </p>
     *
     * @param array 长度为 4 的 {@code int} 数组
     * @return 对应的 {@link UUID}
     */
    public static UUID uuidFromIntArray(int[] array) {
        return new UUID((long) array[0] << 32 | (long) array[1] & 4294967295L, (long) array[2] << 32 | (long) array[3] & 4294967295L);
    }

    /**
     * 将 {@link UUID} 转换为长度为 4 的 int[]
     * <p>
     * 返回的数组布局为 {@code [mostHigh, mostLow, leastHigh, leastLow]},
     * 可通过 {@link #uuidFromIntArray(int[])} 还原为原始 {@link UUID}.
     * </p>
     *
     * @param uuid 要转换的 {@link UUID}，不可为 {@code null}
     * @return 长度为 4 的 {@code int} 数组
     */
    public static int[] uuidToIntArray(UUID uuid) {
        long uuidMost = uuid.getMostSignificantBits();
        long uuidLeast = uuid.getLeastSignificantBits();
        return new int[]{(int) (uuidMost >> 32), (int) uuidMost, (int) (uuidLeast >> 32), (int) uuidLeast};
    }
}
