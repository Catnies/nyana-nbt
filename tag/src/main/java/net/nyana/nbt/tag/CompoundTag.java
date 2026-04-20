package net.nyana.nbt.tag;

import net.nyana.nbt.NBT;
import net.nyana.nbt.tag.visitor.TagVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class CompoundTag implements Tag {
    private static final int SELF_SIZE_IN_BYTES = 48;
    private static final int MAP_ENTRY_SIZE_IN_BYTES = 32;
    public final Map<String, Tag> tags;

    public CompoundTag(Map<String, Tag> tags) {
        this.tags = tags;
    }

    public CompoundTag() {
        this.tags = new HashMap<>(8, 0.8f);
    }

    @Override
    public byte getId() {
        return TAG_COMPOUND;
    }

    @Override
    public @NotNull TagType<?> getType() {
        return TagTypes.COMPOUND;
    }

    @Override
    public void write(@NotNull DataOutput output) throws IOException {
        for (Map.Entry<String, Tag> entry : tags.entrySet()) {
            writeNamedTag(entry.getKey(), entry.getValue(), output);
        }
        output.writeByte(TAG_END);
    }

    private static void writeNamedTag(String key, Tag element, DataOutput output) throws IOException {
        output.writeByte(element.getId());
        if (element.getId() != Tag.TAG_END) {
            output.writeUTF(key);
            element.write(output);
        }
    }

    @Override
    public @NotNull CompoundTag copy() {
        Map<String, Tag> newTags = new HashMap<>(tags.size(), 0.8f);
        for (Map.Entry<String, Tag> entry : tags.entrySet()) {
            newTags.put(entry.getKey(), entry.getValue().copy());
        }
        return new CompoundTag(newTags);
    }

    @Override
    public @NotNull Tag deepClone() {
        Map<String, Tag> newTags = new HashMap<>(tags.size(), 0.8f);
        for (Map.Entry<String, Tag> entry : tags.entrySet()) {
            newTags.put(entry.getKey(), entry.getValue().deepClone());
        }
        return new CompoundTag(newTags);
    }

    @Override
    public int sizeInBytes() {
        int size = SELF_SIZE_IN_BYTES;

        for (Map.Entry<String, Tag> entry : this.tags.entrySet()) {
            size += 28 + 2 * entry.getKey().length();
            size += 36;
            size += entry.getValue().sizeInBytes();
        }

        return size;
    }

    @Override
    public void accept(@NotNull TagVisitor visitor) {
        visitor.visitCompound(this);
    }

    /**
     * 从提供的 DataInput 流中读取具名标签的类型 ID.
     *
     * @param input 要读取的 DataInput 流
     * @return 表示标签类型的 byte ID
     * @throws IOException 如果读取时发生 I/O 错误
     */
    static byte readNamedTagType(@NotNull DataInput input) throws IOException {
        return input.readByte();
    }

    /**
     * 从提供的 DataInput 流中读取具名标签的名称.
     *
     * @param input 要读取的 DataInput 流
     * @return 表示标签名称的字符串
     * @throws IOException 如果读取时发生 I/O 错误
     */
    static @NotNull String readNamedTagName(@NotNull DataInput input) throws IOException {
        return input.readUTF();
    }

    /**
     * 使用指定的 TagType 读取器和深度读取具名标签的数据.
     * 将所有 IOException 包装为 RuntimeException 以简化错误处理.
     *
     * @param reader 负责读取标签数据的 TagType
     * @param input  要读取的 DataInput 流
     * @param depth  标签结构的当前深度 (用于递归读取)
     * @return 从输入中读取到的 Tag 实例
     * @throws RuntimeException 如果读取时发生 IOException
     */
    static @NotNull Tag readNamedTagData(@NotNull TagType<?> reader, @NotNull DataInput input, int depth) {
        try {
            return reader.read(input, depth);
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    /**
     * 添加或替换指定键关联的标签.
     *
     * @param key     与标签关联的键
     * @param element 要存储的标签
     * @return 该键之前关联的标签, 若不存在则返回 null
     */
    public @Nullable Tag put(@NotNull String key, @Nullable Tag element) {
        if (element == null) {
            return this.tags.remove(key);
        }else {
            return this.tags.put(key, element);
        }
    }

    /**
     * 向复合标签中添加指定键关联的 boolean 值.
     * boolean 值以 ByteTag 存储 (true 为 1, false 为 0).
     *
     * @param key   与 boolean 值关联的键
     * @param value 要存储的 boolean 值
     */
    public void putBoolean(@NotNull String key, boolean value) {
        this.tags.put(key, new ByteTag(value));
    }

    public void putByte(@NotNull String key, byte value) {
        this.tags.put(key, new ByteTag(value));
    }

    public void putShort(@NotNull String key, short value) {
        this.tags.put(key, new ShortTag(value));
    }

    public void putInt(@NotNull String key, int value) {
        this.tags.put(key, new IntTag(value));
    }

    public void putLong(@NotNull String key, long value) {
        this.tags.put(key, new LongTag(value));
    }

    public void putFloat(@NotNull String key, float value) {
        this.tags.put(key, new FloatTag(value));
    }

    public void putDouble(@NotNull String key, double value) {
        this.tags.put(key, new DoubleTag(value));
    }

    public void putString(@NotNull String key, @NotNull String value) {
        this.tags.put(key, new StringTag(value));
    }

    public void putByteArray(@NotNull String key, byte @NotNull [] value) {
        this.tags.put(key, new ByteArrayTag(value));
    }

    public void putLongArray(@NotNull String key, long @NotNull [] value) {
        this.tags.put(key, new LongArrayTag(value));
    }

    public void putIntArray(@NotNull String key, int @NotNull [] value) {
        this.tags.put(key, new IntArrayTag(value));
    }

    public void putUUID(@NotNull String key, @NotNull UUID value) {
        this.tags.put(key, NBT.createUUID(value));
    }

    /**
     * 获取与指定键关联的标签.
     *
     * @param key 要查找的键
     * @return 与该键关联的标签, 若不存在则返回 null
     */
    @Nullable
    public Tag get(@NotNull String key) {
        return this.tags.get(key);
    }

    /**
     * 获取与指定键关联的 boolean 值.
     * 若键不存在或类型不兼容, 默认返回 false.
     *
     * @param key 要查找的键
     * @return 与该键关联的 boolean 值
     */
    public boolean getBoolean(@NotNull String key) {
        return getBoolean(key, false);
    }

    /**
     * 获取与指定键关联的 boolean 值, 支持指定默认值.
     *
     * @param key          要查找的键
     * @param defaultValue 若键不存在时返回的默认值
     * @return 与该键关联的 boolean 值, 或默认值
     */
    public boolean getBoolean(@NotNull String key, boolean defaultValue) {
        return getByte(key, (byte) (defaultValue ? 1 : 0)) != 0;
    }

    public byte getByte(@NotNull String key) {
        return getByte(key, (byte) 0);
    }

    public byte getByte(@NotNull String key, byte defaultValue) {
        Byte value = getOrDefault(key, TAG_ANY_NUMERIC, t -> ((NumericTag) t).getAsByte(), defaultValue);
        assert value != null;
        return value;
    }

    public short getShort(@NotNull String key) {
        return getShort(key, (short) 0);
    }

    public short getShort(@NotNull String key, short defaultValue) {
        Short value = getOrDefault(key, TAG_ANY_NUMERIC, t -> ((NumericTag) t).getAsShort(), defaultValue);
        assert value != null;
        return value;
    }

    public int getInt(@NotNull String key) {
        return getInt(key, 0);
    }

    public int getInt(@NotNull String key, int defaultValue) {
        Integer value = getOrDefault(key, TAG_ANY_NUMERIC, t -> ((NumericTag) t).getAsInt(), defaultValue);
        assert value != null;
        return value;
    }

    public long getLong(@NotNull String key) {
        return getLong(key, 0L);
    }

    public long getLong(@NotNull String key, long defaultValue) {
        Long value = getOrDefault(key, TAG_ANY_NUMERIC, t -> ((NumericTag) t).getAsLong(), defaultValue);
        assert value != null;
        return value;
    }

    public float getFloat(@NotNull String key) {
        return getFloat(key, 0f);
    }

    public float getFloat(@NotNull String key, float defaultValue) {
        Float value = getOrDefault(key, TAG_ANY_NUMERIC, t -> ((NumericTag) t).getAsFloat(), defaultValue);
        assert value != null;
        return value;
    }

    public double getDouble(@NotNull String key) {
        return getDouble(key, 0d);
    }

    public double getDouble(@NotNull String key, double defaultValue) {
        Double value = getOrDefault(key, TAG_ANY_NUMERIC, t -> ((NumericTag) t).getAsDouble(), defaultValue);
        assert value != null;
        return value;
    }

    public @Nullable String getString(@NotNull String key) {
        return getString(key, null);
    }

    public @Nullable String getString(@NotNull String key, @Nullable String defaultValue) {
        return getOrDefault(key, TAG_STRING, Tag::getAsString, defaultValue);
    }

    public byte @Nullable[] getByteArray(@NotNull String key) {
        return getByteArray(key, null);
    }

    public byte @Nullable [] getByteArray(@NotNull String key, byte @Nullable [] defaultValue) {
        return getOrDefault(key, TAG_BYTE_ARRAY, t -> ((ByteArrayTag) t).getAsByteArray(), defaultValue);
    }

    public int @Nullable [] getIntArray(@NotNull String key) {
        return getIntArray(key, null);
    }

    public int @Nullable [] getIntArray(@NotNull String key, int @Nullable [] defaultValue) {
        return getOrDefault(key, TAG_INT_ARRAY, t -> ((IntArrayTag) t).getAsIntArray(), defaultValue);
    }

    public long @Nullable [] getLongArray(@NotNull String key) {
        return getLongArray(key, null);
    }

    public long @Nullable [] getLongArray(@NotNull String key, long @Nullable [] defaultValue) {
        return getOrDefault(key, TAG_LONG_ARRAY, t -> ((LongArrayTag) t).getAsLongArray(), defaultValue);
    }

    public @Nullable UUID getUUID(@NotNull String key) {
        return getUUID(key, null);
    }

    public @Nullable UUID getUUID(@NotNull String key, @Nullable UUID defaultValue) {
        return getOrDefault(key, TAG_INT_ARRAY, t -> ((IntArrayTag) t).getAsUUID(), defaultValue);
    }

    public @Nullable CompoundTag getCompound(@NotNull String key) {
        return getCompound(key, null);
    }

    public @Nullable CompoundTag getCompound(@NotNull String key, @Nullable CompoundTag defaultValue) {
        return getOrDefault(key, TAG_COMPOUND, t -> (CompoundTag) t, defaultValue);
    }

    public @Nullable ListTag getList(@NotNull String key) {
        return getList(key, null);
    }

    public @Nullable ListTag getList(@NotNull String key, @Nullable ListTag defaultValue) {
        return getOrDefault(key, TAG_LIST, t -> (ListTag) t, defaultValue);
    }

    /**
     * 根据一段 Keys 获取 boolean 值.
     * 若中途键不存在或类型不兼容, 默认返回 false.
     *
     * @param keys 目标键数组
     * @return 与该键关联的 boolean 值
     */
    public boolean getBoolean(@NotNull String[] keys) {
        return getBoolean(keys, false);
    }

    /**
     * 根据一段 Keys 获取 boolean 值, 支持指定默认值.
     *
     * @param keys         目标键数组
     * @param defaultValue 若中途键不存在或类型不兼容时返回的默认值
     * @return 与该键关联的 boolean 值, 或默认值
     */
    public boolean getBoolean(@NotNull String[] keys, boolean defaultValue) {
        return getByte(keys, (byte) (defaultValue ? 1 : 0)) != 0;
    }

    public byte getByte(@NotNull String[] keys) {
        return getByte(keys, (byte) 0);
    }

    public byte getByte(@NotNull String[] keys, byte defaultValue) {
        Byte value = getOrDefault(keys, TAG_ANY_NUMERIC, t -> ((NumericTag) t).getAsByte(), defaultValue);
        assert value != null;
        return value;
    }

    public short getShort(@NotNull String[] keys) {
        return getShort(keys, (short) 0);
    }

    public short getShort(@NotNull String[] keys, short defaultValue) {
        Short value = getOrDefault(keys, TAG_ANY_NUMERIC, t -> ((NumericTag) t).getAsShort(), defaultValue);
        assert value != null;
        return value;
    }

    public int getInt(@NotNull String[] keys) {
        return getInt(keys, 0);
    }

    public int getInt(@NotNull String[] keys, int defaultValue) {
        Integer value = getOrDefault(keys, TAG_ANY_NUMERIC, t -> ((NumericTag) t).getAsInt(), defaultValue);
        assert value != null;
        return value;
    }

    public long getLong(@NotNull String[] keys) {
        return getLong(keys, 0L);
    }

    public long getLong(@NotNull String[] keys, long defaultValue) {
        Long value = getOrDefault(keys, TAG_ANY_NUMERIC, t -> ((NumericTag) t).getAsLong(), defaultValue);
        assert value != null;
        return value;
    }

    public float getFloat(@NotNull String[] keys) {
        return getFloat(keys, 0f);
    }

    public float getFloat(@NotNull String[] keys, float defaultValue) {
        Float value = getOrDefault(keys, TAG_ANY_NUMERIC, t -> ((NumericTag) t).getAsFloat(), defaultValue);
        assert value != null;
        return value;
    }

    public double getDouble(@NotNull String[] keys) {
        return getDouble(keys, 0d);
    }

    public double getDouble(@NotNull String[] keys, double defaultValue) {
        Double value = getOrDefault(keys, TAG_ANY_NUMERIC, t -> ((NumericTag) t).getAsDouble(), defaultValue);
        assert value != null;
        return value;
    }

    public @Nullable String getString(@NotNull String[] keys) {
        return getString(keys, null);
    }

    public @Nullable String getString(@NotNull String[] keys, @Nullable String defaultValue) {
        return getOrDefault(keys, TAG_STRING, Tag::getAsString, defaultValue);
    }

    public byte @Nullable[] getByteArray(@NotNull String[] keys) {
        return getByteArray(keys, null);
    }

    public byte @Nullable [] getByteArray(@NotNull String[] keys, byte @Nullable [] defaultValue) {
        return getOrDefault(keys, TAG_BYTE_ARRAY, t -> ((ByteArrayTag) t).getAsByteArray(), defaultValue);
    }

    public int @Nullable [] getIntArray(@NotNull String[] keys) {
        return getIntArray(keys, null);
    }

    public int @Nullable [] getIntArray(@NotNull String[] keys, int @Nullable [] defaultValue) {
        return getOrDefault(keys, TAG_INT_ARRAY, t -> ((IntArrayTag) t).getAsIntArray(), defaultValue);
    }

    public long @Nullable [] getLongArray(@NotNull String[] keys) {
        return getLongArray(keys, null);
    }

    public long @Nullable [] getLongArray(@NotNull String[] keys, long @Nullable [] defaultValue) {
        return getOrDefault(keys, TAG_LONG_ARRAY, t -> ((LongArrayTag) t).getAsLongArray(), defaultValue);
    }

    public @Nullable UUID getUUID(@NotNull String[] keys) {
        return getUUID(keys, null);
    }

    public @Nullable UUID getUUID(@NotNull String[] keys, @Nullable UUID defaultValue) {
        return getOrDefault(keys, TAG_INT_ARRAY, t -> ((IntArrayTag) t).getAsUUID(), defaultValue);
    }

    public @Nullable CompoundTag getCompound(@NotNull String[] keys) {
        return getCompound(keys, null);
    }

    public @Nullable CompoundTag getCompound(@NotNull String[] keys, @Nullable CompoundTag defaultValue) {
        return getOrDefault(keys, TAG_COMPOUND, t -> (CompoundTag) t, defaultValue);
    }

    public @Nullable ListTag getList(@NotNull String[] keys) {
        return getList(keys, null);
    }

    public @Nullable ListTag getList(@NotNull String[] keys, @Nullable ListTag defaultValue) {
        return getOrDefault(keys, TAG_LIST, t -> (ListTag) t, defaultValue);
    }

    /**
     * 根据 Key 获取值, 若键不存在或类型不兼容, 默认返回 defaultValue.
     *
     * @param key 目标键
     * @param expectedType 目标键类型
     * @param extractor 提取器
     * @param defaultValue 默认值
     * @return 与该键关联的值
     */
    private <T> @Nullable T getOrDefault(@NotNull String key, int expectedType, @NotNull Function<Tag, T> extractor, @Nullable T defaultValue) {
        Tag tag = tags.get(key);
        return tag != null && tag.isTypeOf(expectedType) ? extractor.apply(tag) : defaultValue;
    }

    /**
     * 根据一段 Keys 获取值, 若中途键不存在或类型不兼容, 默认返回 defaultValue.
     *
     * @param keys 目标键
     * @param expectedType 目标键类型
     * @param extractor 提取器
     * @param defaultValue 默认值
     * @return 与该键关联的值
     */
    private <T> @Nullable T getOrDefault(@NotNull String[] keys, int expectedType, @NotNull Function<Tag, T> extractor, @Nullable T defaultValue) {
        if (keys == null || keys.length == 0) {
            return defaultValue;
        }

        CompoundTag current = this;
        for (int i = 0; i < keys.length - 1; i++) {
            Tag next = current.get(keys[i]);
            if (next instanceof CompoundTag childCompound) {
                current = childCompound;
            } else {
                return defaultValue;
            }
        }

        Tag resultTag = current.get(keys[keys.length - 1]);
        if (resultTag != null && resultTag.isTypeOf(expectedType)) {
            return extractor.apply(resultTag);
        } else {
            return defaultValue;
        }
    }

    public byte getTagType(@NotNull String key) {
        Tag tag = this.tags.get(key);
        return tag == null ? 0 : tag.getId();
    }

    public int size() {
        return tags.size();
    }

    public @NotNull Set<String> keySet() {
        return this.tags.keySet();
    }

    public @NotNull Set<Map.Entry<String, Tag>> entrySet() {
        return this.tags.entrySet();
    }

    /**
     * 检查复合标签是否为空.
     *
     * @return 若复合标签不包含任何键值对则返回 true, 否则返回 false
     */
    public boolean isEmpty() {
        return this.tags.isEmpty();
    }

    /**
     * 从复合标签中移除与指定键关联的标签.
     * 若键不存在, 此方法不执行任何操作.
     *
     * @param key 要移除的标签对应的键
     */
    public void remove(@NotNull String key) {
        this.tags.remove(key);
    }

    /**
     * 检查复合标签是否包含指定的键.
     *
     * @param key 要检查的键
     * @return 若复合标签包含该键则返回 true, 否则返回 false
     */
    public boolean containsKey(@NotNull String key) {
        return this.tags.containsKey(key);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompoundTag that)) return false;
        return Objects.equals(tags, that.tags);
    }

    @Override
    public int hashCode() {
        return tags.hashCode();
    }

    @Override
    public @NotNull String toString() {
        return this.getAsString();
    }
}
