package net.nyana.nbt.tag;

import net.nyana.nbt.tag.visitor.TagVisitor;import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class ListTag extends CollectionTag<Tag> {
    private static final String WRAPPER_MARKER = "";
    private static final int SELF_SIZE_IN_BYTES = 36;
    private final List<Tag> list;

    public ListTag(@NotNull List<Tag> list) {
        this.list = list;
    }

    public ListTag() {
        this.list = new ArrayList<>();
    }

    /**
     * 尝试对 CompoundTag 进行解包(unwrap).
     * 如果传入的 CompoundTag 满足包裹特征(大小为1且包含 WRAPPER_MARKER 键),
     * 则提取并返回其内部真正的 Tag, 否则直接返回原 CompoundTag.
     *
     * @param tag 需要尝试解包的 CompoundTag.
     * @return 解包后的底层 Tag; 如果该对象并不是一个包裹器, 则直接返回原 CompoundTag.
     */
    public static @NotNull Tag tryUnwrap(@NotNull CompoundTag tag) {
        if (tag.size() == 1) {
            Tag tag1 = tag.get(ListTag.WRAPPER_MARKER);
            if (tag1 != null) {
                return tag1;
            }
        }
        return tag;
    }

    /**
     * 判断一个 CompoundTag 是否为用于兼容异构列表的包裹对象(wrapper).
     * 包裹对象的特征为: 仅包含一个键值对, 且键名为 WRAPPER_MARKER ("").
     *
     * @param tag 需要判断的 CompoundTag.
     * @return 如果是包裹对象则返回 true, 否则返回 false.
     */
    private static boolean isWrapper(@NotNull CompoundTag tag) {
        return tag.size() == 1 && tag.containsKey(ListTag.WRAPPER_MARKER);
    }

    /**
     * 在序列化写入时, 如果需要则对 Tag 进行包裹(wrap), 以维持底层 NBT 列表在协议层面上的同构性.
     * 当列表元素的基准类型被判定为 CompoundTag (ID 为 10) 时:
     * - 如果元素本身就是普通的 CompoundTag (且不是包裹对象), 则无需包裹直接返回.
     * - 如果元素是其他类型的 Tag, 或者它恰好具备包裹特征(为了避免嵌套歧义), 则对其进行包裹.
     *
     * @param elementType 当前 ListTag 所决定的基础序列化元素类型 ID.
     * @param tag 需要被处理的 Tag 元素.
     * @return 原 Tag (无需处理时) 或包裹后的 CompoundTag.
     */
    public static @NotNull Tag wrapIfNeeded(byte elementType, @NotNull Tag tag) {
        if (elementType != 10) { // compound
            return tag;
        } else {
            return tag instanceof CompoundTag compoundTag && !ListTag.isWrapper(compoundTag) ? compoundTag : ListTag.wrapElement(tag);
        }
    }

    /**
     * 将任意类型的 Tag 强制包裹进一个新的 CompoundTag 中.
     * 使用 WRAPPER_MARKER ("") 作为键来存储真正的异构 Tag.
     *
     * @param tag 需要被包裹的底层 Tag.
     * @return 作为兼容性包裹层的 CompoundTag.
     */
    private static @NotNull CompoundTag wrapElement(@NotNull Tag tag) {
        return new CompoundTag(Map.of(ListTag.WRAPPER_MARKER, tag));
    }

    /**
     * 向内部列表中添加一个 Tag. 如果该 Tag 被包裹在 CompoundTag 中, 则先将其解包还原.
     * 这通常用于从 NBT 数据中反序列化(读取) ListTag 时, 剔除伪造的外壳, 还原列表中原本的异构元素.
     *
     * @param tag 要添加到列表中的 Tag (可能是来自旧版存储协议中的被包裹状态).
     */
    public void addAndUnwrap(@NotNull Tag tag) {
        if (tag instanceof CompoundTag compoundTag) {
            this.add(ListTag.tryUnwrap(compoundTag));
        } else {
            this.add(tag);
        }
    }

    /**
     * 识别并返回此 ListTag 应该使用的序列化基础元素类型 ID.
     * 如果列表中所有元素都是相同的类型, 则返回该类型的 ID (纯净的同构列表).
     * 如果列表包含异构元素(多种不同类型混用), 则返回 10 (CompoundTag),
     * 这会触发外层的 wrapIfNeeded 机制, 将所有非 CompoundTag 的元素转化为统一的 CompoundTag 格式再写入磁盘/网络传输.
     *
     * @return 列表中元素的 NBT 类型 ID. 异构情况一律返回 10.
     */
    public byte identifyRawElementType() {
        byte type = 0;
        for (Tag tag : this.list) {
            byte id = tag.getId();
            if (type == 0) {
                type = id;
            } else if (type != id) {
                return 10;
            }
        }
        return type;
    }

    /**
     * ListTag 序列化后的格式为: <b>
     * [Tag类型ID] [List长度] [元素] <b>
     * 其实本质只能存储同一种类型的Tag, 但实际开发中经常需要存储不同类型的Tag (即异构列表).
     * 所以在序列化时, 如果发现有是异构列表, 则会将其每个元素都打包成一个 CompoundTag, 其中这个 CompoundTag 的Key始终为 "", Size也始终为1.
     *
     * @param output 要写入的数据输出流
     * @throws IOException IO 异常
     */
    @Override
    public void write(@NotNull DataOutput output) throws IOException {
        byte type = this.identifyRawElementType();
        output.writeByte(type);
        output.writeInt(this.list.size());
        for (Tag tag : this.list) {
            ListTag.wrapIfNeeded(type, tag).write(output);
        }
    }

    @Override
    public @NotNull Tag get(int index) {
        return this.list.get(index);
    }

    @Override
    public @NotNull Tag set(int index, @NotNull Tag tag) {
        return this.list.set(index, tag);
    }

    @Override
    public void add(int index, @NotNull Tag tag) {
        this.list.add(index, tag);
    }

    @Override
    public @NotNull Tag remove(int index) {
        return this.list.remove(index);
    }

    @Override
    public boolean setTag(int index, @NotNull Tag tag) {
        this.list.set(index, tag);
        return true;
    }

    @Override
    public boolean addTag(int index, @NotNull Tag tag) {
        this.list.add(index, tag);
        return true;
    }

    @Override
    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    @Override
    public int size() {
        return this.list.size();
    }

    @Override
    public byte getId() {
        return Tag.TAG_LIST;
    }

    @Override
    public@NotNull TagType<?> getType() {
        return TagTypes.LIST;
    }

    @Override
    public @NotNull Tag copy() {
        return new ListTag(new ArrayList<>(this.list));
    }

    @Override
    public @NotNull Tag deepClone() {
        List<Tag> list = new ArrayList<>(this.list.size());
        for (Tag tag : this.list) {
            list.add(tag.deepClone());
        }
        return new ListTag(list);
    }

    @Override
    public int sizeInBytes() {
        int size = ListTag.SELF_SIZE_IN_BYTES;
        size += 4 * this.list.size();

        for (Tag child : this.list) {
            size += child.sizeInBytes();
        }

        return size;
    }

    @Override
    public void accept(@NotNull TagVisitor visitor) {
        visitor.visitList(this);
    }

    /**
     * 获取列表中指定索引处的 String 值.
     *
     * @param index 元素的索引
     * @return 指定索引处的 String 值, 若未找到或类型不匹配则返回 {@code null}
     */
    public @Nullable String getString(int index) {
        return this.getString(index, null);
    }

    /**
     * 获取列表中指定索引处的 String 值, 支持指定默认值.
     *
     * @param index 元素的索引
     * @param defaultValue 若元素不是 String 或不存在时返回的默认值
     * @return 指定索引处的 String 值, 或默认值
     */
    public @Nullable String getString(int index, String defaultValue) {
        return this.getTypedValue(index, Tag.TAG_STRING, Tag::getAsString, defaultValue);
    }

    /**
     * 获取列表中指定索引处的 float 值.
     *
     * @param index 元素的索引
     * @return 指定索引处的 float 值, 若未找到或类型不匹配则返回 0
     */
    public float getFloat(int index) {
        return this.getFloat(index, 0f);
    }

    /**
     * 获取列表中指定索引处的 float 值, 支持指定默认值.
     *
     * @param index 元素的索引
     * @param defaultValue 若元素不是 float 或不存在时返回的默认值
     * @return 指定索引处的 float 值, 或默认值
     */
    public float getFloat(int index, float defaultValue) {
        Float value = this.getTypedValue(index, Tag.TAG_FLOAT, t -> ((FloatTag) t).getAsFloat(), defaultValue);
        assert value != null;
        return value;
    }

    /**
     * 获取列表中指定索引处的 double 值.
     *
     * @param index 元素的索引
     * @return 指定索引处的 double 值, 若未找到或类型不匹配则返回 0
     */
    public double getDouble(int index) {
        return this.getDouble(index, 0d);
    }

    /**
     * 获取列表中指定索引处的 double 值, 支持指定默认值.
     *
     * @param index 元素的索引
     * @param defaultValue 若元素不是 double 或不存在时返回的默认值
     * @return 指定索引处的 double 值, 或默认值
     */
    public double getDouble(int index, double defaultValue) {
        Double value = this.getTypedValue(index, Tag.TAG_DOUBLE, t -> ((DoubleTag) t).getAsDouble(), defaultValue);
        assert value != null;
        return value;
    }

    /**
     * 获取列表中指定索引处的 long 数组.
     *
     * @param index 元素的索引
     * @return 指定索引处的 long 数组, 若未找到或类型不匹配则返回 {@code null}
     */
    public long @Nullable[] getLongArray(int index) {
        return this.getLongArray(index, null);
    }

    /**
     * 获取列表中指定索引处的 long 数组, 支持指定默认值.
     *
     * @param index 元素的索引
     * @param defaultValue 若元素不是 long 数组或不存在时返回的默认值
     * @return 指定索引处的 long 数组, 或默认值
     */
    public long @Nullable[] getLongArray(int index, long @Nullable[] defaultValue) {
        return this.getTypedValue(index, Tag.TAG_LONG_ARRAY, t -> ((LongArrayTag) t).getAsLongArray(), defaultValue);
    }

    /**
     * 获取列表中指定索引处的 int 数组.
     *
     * @param index 元素的索引
     * @return 指定索引处的 int 数组, 若未找到或类型不匹配则返回 {@code null}
     */
    public int @Nullable[] getIntArray(int index) {
        return this.getIntArray(index, null);
    }

    /**
     * 获取列表中指定索引处的 int 数组, 支持指定默认值.
     *
     * @param index 元素的索引
     * @param defaultValue 若元素不是 int 数组或不存在时返回的默认值
     * @return 指定索引处的 int 数组, 或默认值
     */
    public int @Nullable[] getIntArray(int index, int @Nullable[] defaultValue) {
        return this.getTypedValue(index, Tag.TAG_INT_ARRAY, t -> ((IntArrayTag) t).getAsIntArray(), defaultValue);
    }

    /**
     * 获取列表中指定索引处的 int 值.
     *
     * @param index 元素的索引
     * @return 指定索引处的 int 值, 若未找到或类型不匹配则返回 0
     */
    public int getInt(int index) {
        return this.getInt(index, 0);
    }

    /**
     * 获取列表中指定索引处的 int 值, 支持指定默认值.
     *
     * @param index 元素的索引
     * @param defaultValue 若元素不是 int 或不存在时返回的默认值
     * @return 指定索引处的 int 值, 或默认值
     */
    public int getInt(int index, int defaultValue) {
        Integer value = this.getTypedValue(index, Tag.TAG_INT, t -> ((IntTag) t).getAsInt(), defaultValue);
        assert value != null;
        return value;
    }

    /**
     * 获取列表中指定索引处的 short 值.
     *
     * @param index 元素的索引
     * @return 指定索引处的 short 值, 若未找到或类型不匹配则返回 0
     */
    public short getShort(int index) {
        return this.getShort(index, (short) 0);
    }

    /**
     * 获取列表中指定索引处的 short 值, 支持指定默认值.
     *
     * @param index 元素的索引
     * @param defaultValue 若元素不是 short 或不存在时返回的默认值
     * @return 指定索引处的 short 值, 或默认值
     */
    public short getShort(int index, short defaultValue) {
        Short value = this.getTypedValue(index, Tag.TAG_SHORT, t -> ((ShortTag) t).getAsShort(), defaultValue);
        assert value != null;
        return value;
    }

    /**
     * 获取列表中指定索引处的 byte 值.
     *
     * @param index 元素的索引
     * @return 指定索引处的 byte 值, 若未找到或类型不匹配则返回 0
     */
    public byte getByte(int index) {
        return this.getByte(index, (byte) 0);
    }

    /**
     * 获取列表中指定索引处的 byte 值, 支持指定默认值.
     *
     * @param index 元素的索引
     * @param defaultValue 若元素不是 byte 或不存在时返回的默认值
     * @return 指定索引处的 byte 值, 或默认值
     */
    public byte getByte(int index, byte defaultValue) {
        Byte value = this.getTypedValue(index, Tag.TAG_BYTE, t -> ((ByteTag) t).getAsByte(), defaultValue);
        assert value != null;
        return value;
    }

    /**
     * 获取列表中指定索引处的 long 值.
     *
     * @param index 元素的索引
     * @return 指定索引处的 long 值, 若未找到或类型不匹配则返回 0
     */
    public long getLong(int index) {
        return this.getLong(index, 0);
    }

    /**
     * 获取列表中指定索引处的 long 值, 支持指定默认值.
     *
     * @param index 元素的索引
     * @param defaultValue 若元素不是 long 或不存在时返回的默认值
     * @return 指定索引处的 long 值, 或默认值
     */
    public long getLong(int index, long defaultValue) {
        Long value = this.getTypedValue(index, Tag.TAG_LONG, t -> ((LongTag) t).getAsLong(), defaultValue);
        assert value != null;
        return value;
    }

    /**
     * 获取列表中指定索引处的 ListTag.
     *
     * @param index 元素的索引
     * @return 指定索引处的 ListTag, 若未找到或类型不匹配则返回 {@code null}
     */
    public @Nullable ListTag getList(int index) {
        return this.getList(index, null);
    }

    /**
     * 获取列表中指定索引处的 ListTag, 支持指定默认值.
     *
     * @param index 元素的索引
     * @param defaultValue 若元素不是 ListTag 或不存在时返回的默认值
     * @return 指定索引处的 ListTag, 或默认值
     */
    public @Nullable ListTag getList(int index, @Nullable ListTag defaultValue) {
        return this.getTypedValue(index, Tag.TAG_LIST, t -> (ListTag) t, defaultValue);
    }

    /**
     * 获取列表中指定索引处的 CompoundTag.
     *
     * @param index 元素的索引
     * @return 指定索引处的 CompoundTag, 若未找到或类型不匹配则返回 {@code null}
     */
    public @Nullable CompoundTag getCompound(int index) {
        return this.getCompound(index, null);
    }

    /**
     * 获取列表中指定索引处的 CompoundTag, 支持指定默认值.
     *
     * @param index 元素的索引
     * @param defaultValue 若元素不是 CompoundTag 或不存在时返回的默认值
     * @return 指定索引处的 CompoundTag, 或默认值
     */
    public @Nullable CompoundTag getCompound(int index, @Nullable CompoundTag defaultValue) {
        return this.getTypedValue(index, Tag.TAG_COMPOUND, t -> (CompoundTag) t, defaultValue);
    }

    private <T> @Nullable T getTypedValue(int index, int expectedId, @NotNull Function<Tag, T> extractor, @Nullable T defaultValue) {
        if (index >= 0 && index < this.list.size()) {
            Tag tag = this.list.get(index);
            if (tag.getId() == expectedId) {
                return extractor.apply(tag);
            }
        }
        return defaultValue;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ListTag tags)) return false;
        return Objects.equals(this.list, tags.list);
    }

    @Override
    public int hashCode() {
        return this.list.hashCode();
    }
}
