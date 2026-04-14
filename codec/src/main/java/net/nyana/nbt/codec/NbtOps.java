package net.nyana.nbt.codec;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import net.nyana.nbt.tag.*;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.*;

/**
 * NBT 格式的 {@link DynamicOps} 实现.
 * <p>
 * 将 Mojang DataFixerUpper 的序列化/反序列化框架适配到 NBT 标签体系,
 * 使得任意可被 DFU Codec 描述的数据结构都能与 NBT 互相转换.
 */
public class NbtOps implements DynamicOps<Tag> {
    public static final NbtOps INSTANCE = new NbtOps();

    /**
     * 返回当前 {@link DynamicOps} 的空值表示.
     *
     * @return NBT 体系中的空值 {@link EndTag}.
     */
    @Override
    public Tag empty() {
        return EndTag.INSTANCE;
    }

    /**
     * 将一个 NBT {@link Tag} 转换到另一种 {@link DynamicOps} 表示.
     * 根据标签的类型 ID 分发到目标 ops 的对应 create 方法.
     *
     * @param dynamicOps 目标 DynamicOps 实现.
     * @param tag 待转换的 NBT 标签.
     * @return 转换后的目标类型对象.
     */
    @Override
    public <U> U convertTo(DynamicOps<U> dynamicOps, Tag tag) {
        return switch (tag.getId()) {
            case Tag.TAG_END        -> dynamicOps.empty();
            case Tag.TAG_BYTE       -> dynamicOps.createByte(((NumericTag) tag).getAsByte());
            case Tag.TAG_SHORT      -> dynamicOps.createShort(((NumericTag) tag).getAsShort());
            case Tag.TAG_INT        -> dynamicOps.createInt(((NumericTag) tag).getAsInt());
            case Tag.TAG_LONG       -> dynamicOps.createLong(((NumericTag) tag).getAsLong());
            case Tag.TAG_FLOAT      -> dynamicOps.createFloat(((NumericTag) tag).getAsFloat());
            case Tag.TAG_DOUBLE     -> dynamicOps.createDouble(((NumericTag) tag).getAsDouble());
            case Tag.TAG_BYTE_ARRAY -> dynamicOps.createByteList(ByteBuffer.wrap(((ByteArrayTag) tag).getAsByteArray()));
            case Tag.TAG_STRING     -> dynamicOps.createString(tag.getAsString());
            case Tag.TAG_LIST       -> this.convertList(dynamicOps, tag);
            case Tag.TAG_COMPOUND   -> this.convertMap(dynamicOps, tag);
            case Tag.TAG_INT_ARRAY  -> dynamicOps.createIntList(Arrays.stream(((IntArrayTag) tag).getAsIntArray()));
            case Tag.TAG_LONG_ARRAY -> dynamicOps.createLongList(Arrays.stream(((LongArrayTag) tag).getAsLongArray()));
            default -> throw new IllegalStateException("Unknown tag type: " + tag);
        };
    }

    /**
     * 创建 {@link ByteTag}.
     *
     * @param b 字节值.
     * @return 对应的 NBT 标签.
     */
    @Override
    public Tag createByte(byte b) {
        return new ByteTag(b);
    }

    /**
     * 创建 {@link ShortTag}.
     *
     * @param s 短整型值.
     * @return 对应的 NBT 标签.
     */
    @Override
    public Tag createShort(short s) {
        return new ShortTag(s);
    }

    /**
     * 创建 {@link IntTag}.
     *
     * @param i 整型值.
     * @return 对应的 NBT 标签.
     */
    @Override
    public Tag createInt(int i) {
        return new IntTag(i);
    }

    /**
     * 创建 {@link LongTag}.
     *
     * @param l 长整型值.
     * @return 对应的 NBT 标签.
     */
    @Override
    public Tag createLong(long l) {
        return new LongTag(l);
    }

    /**
     * 创建 {@link FloatTag}.
     *
     * @param f 浮点值.
     * @return 对应的 NBT 标签.
     */
    @Override
    public Tag createFloat(float f) {
        return new FloatTag(f);
    }

    /**
     * 创建 {@link DoubleTag}.
     *
     * @param d 双精度值.
     * @return 对应的 NBT 标签.
     */
    @Override
    public Tag createDouble(double d) {
        return new DoubleTag(d);
    }

    /**
     * 创建布尔值对应的 NBT 标签.
     * NBT 不提供原生布尔类型, 因此使用 {@link ByteTag} 表示.
     *
     * @param b 布尔值.
     * @return 值为 0 或 1 的 {@link ByteTag}.
     */
    @Override
    public Tag createBoolean(boolean b) {
        return new ByteTag(b);
    }

    /**
     * 创建 {@link StringTag}.
     *
     * @param string 字符串值.
     * @return 对应的 NBT 标签.
     */
    @Override
    public Tag createString(String string) {
        return new StringTag(string);
    }

    /**
     * 创建数值标签.
     * 当前实现统一将任意 {@link Number} 转换为 {@link DoubleTag}.
     *
     * @param number 数值对象.
     * @return 对应的 NBT 标签.
     */
    @Override
    public Tag createNumeric(Number number) {
        return new DoubleTag(number.doubleValue());
    }


    /**
     * 从 {@link IntStream} 创建 {@link IntArrayTag}.
     *
     * @param data 整型流.
     * @return 对应的 NBT 标签.
     */
    @Override
    public Tag createIntList(IntStream data) {
        return new IntArrayTag(data.toArray());
    }

    /**
     * 从 {@link ByteBuffer} 创建 {@link ByteArrayTag}.
     *
     * @param data 字节缓冲区.
     * @return 对应的 NBT 标签.
     */
    @Override
    public Tag createByteList(ByteBuffer data) {
        ByteBuffer byteBuffer = data.duplicate().clear();
        byte[] bytes = new byte[data.capacity()];
        byteBuffer.get(0, bytes, 0, bytes.length);
        return new ByteArrayTag(bytes);
    }

    /**
     * 从 {@link LongStream} 创建 {@link LongArrayTag}.
     *
     * @param data 长整型流.
     * @return 对应的 NBT 标签.
     */
    @Override
    public Tag createLongList(LongStream data) {
        return new LongArrayTag(data.toArray());
    }

    /**
     * 从标签流创建 {@link ListTag}.
     *
     * @param data 标签流.
     * @return 对应的 NBT 标签.
     */
    @Override
    public Tag createList(Stream<Tag> data) {
        return new ListTag(data.collect(toMutableList()));
    }

    /**
     * 提取标签中的数值内容.
     *
     * @param tag 待读取的标签.
     * @return 成功时返回数值, 否则返回错误结果.
     */
    @Override
    public DataResult<Number> getNumberValue(Tag tag) {
        if (tag instanceof NumericTag numericTag) {
            return DataResult.success(numericTag.getAsNumber());
        }
        return DataResult.error(() -> "Not a number");
    }

    /**
     * 提取标签中的字符串内容.
     *
     * @param tag 待读取的标签.
     * @return 成功时返回字符串, 否则返回错误结果.
     */
    @Override
    public DataResult<String> getStringValue(Tag tag) {
        if (tag instanceof StringTag stringTag) {
            return DataResult.success(stringTag.getAsString());
        }
        return DataResult.error(() -> "Not a string");
    }

    /**
     * 将单个元素追加到列表标签中.
     * 如果 tag 是 {@link EndTag} 或 {@link CollectionTag}, 则创建对应的收集器并追加元素;
     * 否则返回错误.
     *
     * @param tag 目标列表标签.
     * @param tag2 待追加的元素.
     * @return 合并后的标签, 或错误结果.
     */
    @Override
    public DataResult<Tag> mergeToList(Tag tag, Tag tag2) {
        return createCollector(tag)
                .map(collector -> DataResult.success(collector.accept(tag2).result()))
                .orElseGet(() -> DataResult.error(() -> "mergeToList called with not a list: " + tag, tag));
    }

    /**
     * 将多个元素批量追加到列表标签中.
     * 如果 tag 是 {@link EndTag} 或 {@link CollectionTag}, 则创建对应的收集器并追加所有元素;
     * 否则返回错误.
     *
     * @param tag 目标列表标签.
     * @param list 待追加的元素列表.
     * @return 合并后的标签, 或错误结果.
     */
    @Override
    public DataResult<Tag> mergeToList(Tag tag, List<Tag> list) {
        return createCollector(tag)
                .map(collector -> DataResult.success(collector.acceptAll(list).result()))
                .orElseGet(() -> DataResult.error(() -> "mergeToList called with not a list: " + tag, tag));
    }

    /**
     * 向 Map 标签中插入一个键值对.
     * map 必须是 {@link CompoundTag} 或 {@link EndTag} (表示空 map),
     * key 必须是 {@link StringTag}.
     *
     * @param map 目标 Map 标签.
     * @param key 键标签.
     * @param value 值标签.
     * @return 合并后的标签, 或错误结果.
     */
    @Override
    public DataResult<Tag> mergeToMap(Tag map, Tag key, Tag value) {
        if (!(map instanceof CompoundTag) && !(map instanceof EndTag)) {
            return DataResult.error(() -> "mergeToMap called with not a map: " + map, map);
        }
        if (!(key instanceof StringTag stringKey)) {
            return DataResult.error(() -> "key is not a string: " + key, map);
        }
        CompoundTag result = (map instanceof CompoundTag existing) ? existing.copy() : new CompoundTag();
        result.put(stringKey.getAsString(), value);
        return DataResult.success(result);
    }

    /**
     * 将 {@link MapLike} 中的所有条目合并到 map 标签中.
     * 所有 key 必须是 {@link StringTag}, 否则收集到错误列表中.
     *
     * @param map 目标 Map 标签.
     * @param otherMap 待合并的映射视图.
     * @return 合并后的标签, 或错误结果.
     */
    @Override
    public DataResult<Tag> mergeToMap(Tag map, MapLike<Tag> otherMap) {
        if (!(map instanceof CompoundTag) && !(map instanceof EndTag)) {
            return DataResult.error(() -> "mergeToMap called with not a map: " + map, map);
        }
        CompoundTag result = (map instanceof CompoundTag existing) ? existing.copy() : new CompoundTag();
        List<Tag> invalidKeys = new ArrayList<>();
        otherMap.entries().forEach(pair -> {
            Tag keyTag = pair.getFirst();
            if (keyTag instanceof StringTag stringKey) {
                result.put(stringKey.getAsString(), pair.getSecond());
            } else {
                invalidKeys.add(keyTag);
            }
        });
        return invalidKeys.isEmpty()
                ? DataResult.success(result)
                : DataResult.error(() -> "Invalid keys: " + invalidKeys, result);
    }

    /**
     * 将 {@link Map} 中的所有条目合并到 map 标签中.
     * 所有 key 必须是 {@link StringTag}, 否则收集到错误列表中.
     *
     * @param map 目标 Map 标签.
     * @param entriesToMerge 待合并的键值对集合.
     * @return 合并后的标签, 或错误结果.
     */
    @Override
    public DataResult<Tag> mergeToMap(Tag map, Map<Tag, Tag> entriesToMerge) {
        if (!(map instanceof CompoundTag) && !(map instanceof EndTag)) {
            return DataResult.error(() -> "mergeToMap called with not a map: " + map, map);
        }
        CompoundTag result = (map instanceof CompoundTag existing) ? existing.copy() : new CompoundTag();
        List<Tag> invalidKeys = new ArrayList<>();
        for (Map.Entry<Tag, Tag> entry : entriesToMerge.entrySet()) {
            Tag keyTag = entry.getKey();
            if (keyTag instanceof StringTag stringKey) {
                result.put(stringKey.getAsString(), entry.getValue());
            } else {
                invalidKeys.add(keyTag);
            }
        }
        return invalidKeys.isEmpty()
                ? DataResult.success(result)
                : DataResult.error(() -> "Found non-string keys: " + invalidKeys, result);
    }

    /**
     * 获取 {@link CompoundTag} 的所有条目, 以 {@code Pair<StringTag, Tag>} 的流形式返回.
     *
     * @param map 待读取的 Map 标签.
     * @return 成功时返回键值对流, 否则返回错误结果.
     */
    @Override
    public DataResult<Stream<Pair<Tag, Tag>>> getMapValues(Tag map) {
        if (map instanceof CompoundTag compoundTag) {
            return DataResult.success(
                    compoundTag.entrySet().stream()
                            .map(entry -> Pair.of(this.createString(entry.getKey()), entry.getValue()))
            );
        }
        return DataResult.error(() -> "Not a map: " + map);
    }

    /**
     * 获取 {@link CompoundTag} 的条目遍历器.
     * 返回一个 Consumer, 调用时会将每个键值对喂给提供的 BiConsumer.
     *
     * @param map 待读取的 Map 标签.
     * @return 成功时返回条目遍历器, 否则返回错误结果.
     */
    @Override
    public DataResult<Consumer<BiConsumer<Tag, Tag>>> getMapEntries(Tag map) {
        if (map instanceof CompoundTag compoundTag) {
            return DataResult.success(biConsumer -> {
                for (Map.Entry<String, Tag> entry : compoundTag.entrySet()) {
                    biConsumer.accept(this.createString(entry.getKey()), entry.getValue());
                }
            });
        }
        return DataResult.error(() -> "Not a map: " + map);
    }

    /**
     * 将 {@link CompoundTag} 包装为 {@link MapLike} 视图, 支持按键查找和条目遍历.
     *
     * @param map 待包装的 Map 标签.
     * @return 成功时返回映射视图, 否则返回错误结果.
     */
    @Override
    public DataResult<MapLike<Tag>> getMap(Tag map) {
        if (!(map instanceof CompoundTag compoundTag)) {
            return DataResult.error(() -> "Not a map: " + map);
        }
        return DataResult.success(new MapLike<>() {
            /**
             * 根据标签形式的键读取对应的值.
             *
             * @param tag 键标签.
             * @return 对应的值标签, 不存在时返回 {@code null}.
             */
            @Nullable
            @Override
            public Tag get(Tag tag) {
                if (tag instanceof StringTag stringTag) {
                    return compoundTag.get(stringTag.getAsString());
                }
                throw new UnsupportedOperationException("Cannot get map entry with non-string key: " + tag);
            }

            /**
             * 根据字符串键读取对应的值.
             *
             * @param string 键名.
             * @return 对应的值标签, 不存在时返回 {@code null}.
             */
            @Nullable
            @Override
            public Tag get(String string) {
                return compoundTag.get(string);
            }

            /**
             * 返回当前映射视图中的所有条目.
             *
             * @return 键值对流.
             */
            @Override
            public Stream<Pair<Tag, Tag>> entries() {
                return compoundTag.entrySet().stream()
                        .map(entry -> Pair.of(NbtOps.this.createString(entry.getKey()), entry.getValue()));
            }

            /**
             * 返回当前映射视图的字符串表示.
             *
             * @return 调试用字符串.
             */
            @Override
            public String toString() {
                return "MapLike[" + compoundTag + "]";
            }
        });
    }

    /**
     * 从键值对流创建 {@link CompoundTag}.
     * 所有 key 必须是 {@link StringTag}, 否则抛出异常.
     *
     * @param data 键值对流.
     * @return 构建后的复合标签.
     */
    @Override
    public Tag createMap(Stream<Pair<Tag, Tag>> data) {
        CompoundTag compoundTag = new CompoundTag();
        data.forEach(pair -> {
            if (pair.getFirst() instanceof StringTag stringTag) {
                compoundTag.put(stringTag.getAsString(), pair.getSecond());
            } else {
                throw new UnsupportedOperationException("Cannot create map with non-string key: " + pair.getFirst());
            }
        });
        return compoundTag;
    }

    /**
     * 将 {@link CollectionTag} 转换为标签流.
     *
     * @param tag 待读取的标签.
     * @return 成功时返回标签流, 否则返回错误结果.
     */
    @SuppressWarnings("unchecked")
    @Override
    public DataResult<Stream<Tag>> getStream(Tag tag) {
        if (tag instanceof CollectionTag<?> collectionTag) {
            return DataResult.success(((CollectionTag<Tag>) collectionTag).stream());
        }
        return DataResult.error(() -> "Not a list");
    }

    /**
     * 将 {@link CollectionTag} 转换为元素遍历器.
     *
     * @param tag 待读取的标签.
     * @return 成功时返回遍历器, 否则返回错误结果.
     */
    @Override
    public DataResult<Consumer<Consumer<Tag>>> getList(Tag tag) {
        if (tag instanceof CollectionTag<?> collectionTag) {
            return DataResult.success(collectionTag::forEach);
        }
        return DataResult.error(() -> "Not a list: " + tag);
    }

    /**
     * 尝试从标签中提取 {@link ByteBuffer}.
     * 优先处理 {@link ByteArrayTag} 直接转换;
     * 回退路径: 将列表中的每个元素按数值类型逐个读取为 byte.
     *
     * @param tag 待读取的标签.
     * @return 成功时返回字节缓冲区, 否则返回错误结果.
     */
    @Override
    public DataResult<ByteBuffer> getByteBuffer(Tag tag) {
        if (tag instanceof ByteArrayTag byteArrayTag) {
            return DataResult.success(ByteBuffer.wrap(byteArrayTag.getAsByteArray()));
        }
        return getStream(tag).flatMap(stream -> {
            List<Tag> list = stream.toList();
            byte[] bytes = new byte[list.size()];
            for (int i = 0; i < list.size(); i++) {
                Optional<Number> num = getNumberValue(list.get(i)).result();
                if (num.isEmpty()) {
                    return DataResult.error(() -> "Some elements are not bytes: " + tag);
                }
                bytes[i] = num.get().byteValue();
            }
            return DataResult.success(ByteBuffer.wrap(bytes));
        });
    }

    /**
     * 尝试从标签中提取 {@link IntStream}.
     * 优先处理 {@link IntArrayTag} 直接转换;
     * 回退路径: 将列表中的每个元素按数值类型逐个读取为 int.
     *
     * @param tag 待读取的标签.
     * @return 成功时返回整型流, 否则返回错误结果.
     */
    @Override
    public DataResult<IntStream> getIntStream(Tag tag) {
        if (tag instanceof IntArrayTag intArrayTag) {
            return DataResult.success(Arrays.stream(intArrayTag.getAsIntArray()));
        }
        return getStream(tag).flatMap(stream -> {
            List<Tag> list = stream.toList();
            int[] ints = new int[list.size()];
            for (int i = 0; i < list.size(); i++) {
                Optional<Number> num = getNumberValue(list.get(i)).result();
                if (num.isEmpty()) {
                    return DataResult.error(() -> "Some elements are not ints: " + tag);
                }
                ints[i] = num.get().intValue();
            }
            return DataResult.success(Arrays.stream(ints));
        });
    }

    /**
     * 尝试从标签中提取 {@link LongStream}.
     * 优先处理 {@link LongArrayTag} 直接转换;
     * 回退路径: 将列表中的每个元素按数值类型逐个读取为 long.
     *
     * @param tag 待读取的标签.
     * @return 成功时返回长整型流, 否则返回错误结果.
     */
    @Override
    public DataResult<LongStream> getLongStream(Tag tag) {
        if (tag instanceof LongArrayTag longArrayTag) {
            return DataResult.success(Arrays.stream(longArrayTag.getAsLongArray()));
        }
        return getStream(tag).flatMap(stream -> {
            List<Tag> list = stream.toList();
            long[] longs = new long[list.size()];
            for (int i = 0; i < list.size(); i++) {
                Optional<Number> num = getNumberValue(list.get(i)).result();
                if (num.isEmpty()) {
                    return DataResult.error(() -> "Some elements are not longs: " + tag);
                }
                longs[i] = num.get().longValue();
            }
            return DataResult.success(Arrays.stream(longs));
        });
    }

    /**
     * 创建一个生成可变 {@link List} 的收集器.
     *
     * @param <T> 列表元素类型.
     * @return 可用于流操作的收集器.
     */
    public static <T> Collector<T, ?, List<T>> toMutableList() {
        return Collectors.toCollection(Lists::newArrayList);
    }

    /**
     * 从 {@link CompoundTag} 中移除指定键, 返回新的副本.
     * 若 map 不是 CompoundTag, 则原样返回.
     *
     * @param map 原始 Map 标签.
     * @param removeKey 待移除的键.
     * @return 移除指定键后的标签.
     */
    @Override
    public Tag remove(Tag map, String removeKey) {
        if (map instanceof CompoundTag compoundTag) {
            CompoundTag copied = compoundTag.copy();
            copied.remove(removeKey);
            return copied;
        }
        return map;
    }

    /**
     * 创建用于构建 {@link CompoundTag} 的 {@link RecordBuilder}.
     *
     * @return 记录构建器实例.
     */
    @Override
    public RecordBuilder<Tag> mapBuilder() {
        return new NbtRecordBuilder();
    }

    /**
     * 根据已有标签类型创建对应的 {@link ListCollector}.
     * <ul>
     *   <li>{@link EndTag}: 空列表, 返回通用收集器</li>
     *   <li>空的 {@link CollectionTag}: 同上</li>
     *   <li>{@link ListTag}: 通用收集器(保留已有元素)</li>
     *   <li>{@link ByteArrayTag}/{@link IntArrayTag}/{@link LongArrayTag}: 对应的类型化收集器</li>
     * </ul>
     * 若标签不是列表/数组类型, 返回 {@link Optional#empty()}.
     *
     * @param tag 目标标签.
     * @return 对应的列表收集器, 或空结果.
     */
    private static Optional<ListCollector> createCollector(Tag tag) {
        if (tag instanceof EndTag) {
            return Optional.of(new GenericListCollector());
        }
        if (!(tag instanceof CollectionTag<?> collectionTag)) {
            return Optional.empty();
        }
        if (collectionTag.isEmpty()) {
            return Optional.of(new GenericListCollector());
        }
        return Optional.of(switch (collectionTag) {
            case ListTag listTag       -> new GenericListCollector(listTag);
            case ByteArrayTag byteTag  -> new ByteListCollector(byteTag.getAsByteArray());
            case IntArrayTag intTag    -> new IntListCollector(intTag.getAsIntArray());
            case LongArrayTag longTag  -> new LongListCollector(longTag.getAsLongArray());
            default -> throw new IllegalStateException("Unexpected collection type: " + collectionTag);
        });
    }

    /**
     * 列表收集器接口, 用于在 mergeToList 操作中逐步构建列表标签.
     * 收集器会尽可能保持类型化数组(ByteArray/IntArray/LongArray),
     * 当遇到不匹配的元素类型时, 自动回退为通用 {@link ListTag}.
     */
    interface ListCollector {
        /**
         * 接收一个元素.
         *
         * @param tag 待追加的标签.
         * @return 当前收集器, 或降级后的新收集器.
         */
        ListCollector accept(Tag tag);

        /**
         * 批量接收多个元素.
         *
         * @param tags 待追加的标签集合.
         * @return 追加完成后的收集器.
         */
        default ListCollector acceptAll(Iterable<Tag> tags) {
            ListCollector collector = this;
            for (Tag tag : tags) {
                collector = collector.accept(tag);
            }
            return collector;
        }

        /**
         * 从标签流中批量接收元素.
         *
         * @param tags 待追加的标签流.
         * @return 追加完成后的收集器.
         */
        default ListCollector acceptAll(Stream<Tag> tags) {
            Objects.requireNonNull(tags);
            return this.acceptAll(tags::iterator);
        }

        /**
         * 生成最终的标签结果.
         *
         * @return 收集完成后的标签.
         */
        Tag result();
    }

    /**
     * 通用列表收集器, 内部使用 {@link ListTag} 存储, 可接受任意类型的元素.
     * 当类型化收集器(Byte/Int/Long)遇到不匹配元素时, 会降级转换为此收集器.
     */
    static class GenericListCollector implements ListCollector {
        private final ListTag result = new ListTag();

        /**
         * 创建一个空的通用列表收集器.
         */
        GenericListCollector() {}

        /**
         * 从已有 {@link ListTag} 初始化收集器.
         *
         * @param list 初始列表标签.
         */
        GenericListCollector(ListTag list) {
            this.result.addAll(list);
        }

        /**
         * 从 int 列表初始化收集器.
         * 每个元素都会包装为 {@link IntTag}.
         *
         * @param list 初始 int 列表.
         */
        GenericListCollector(IntArrayList list) {
            list.forEach(i -> this.result.add(new IntTag(i)));
        }

        /**
         * 从 byte 列表初始化收集器.
         * 每个元素都会包装为 {@link ByteTag}.
         *
         * @param list 初始 byte 列表.
         */
        GenericListCollector(ByteArrayList list) {
            list.forEach(b -> this.result.add(new ByteTag(b)));
        }

        /**
         * 从 long 列表初始化收集器.
         * 每个元素都会包装为 {@link LongTag}.
         *
         * @param list 初始 long 列表.
         */
        GenericListCollector(LongArrayList list) {
            list.forEach(l -> this.result.add(new LongTag(l)));
        }

        /**
         * 向通用列表中追加一个元素.
         *
         * @param tag 待追加的标签.
         * @return 当前收集器.
         */
        @Override
        public ListCollector accept(Tag tag) {
            this.result.add(tag);
            return this;
        }

        /**
         * 返回构建完成的列表标签.
         *
         * @return {@link ListTag} 结果.
         */
        @Override
        public Tag result() {
            return this.result;
        }
    }

    /**
     * Byte 类型化列表收集器.
     * 若接收到非 {@link ByteTag} 元素, 自动降级为 {@link GenericListCollector}.
     */
    static class ByteListCollector implements ListCollector {
        private final ByteArrayList values = new ByteArrayList();

        /**
         * 使用已有字节数组初始化收集器.
         *
         * @param values 初始字节数组.
         */
        ByteListCollector(byte[] values) {
            this.values.addElements(0, values);
        }

        /**
         * 追加一个标签.
         * 若标签不是 {@link ByteTag}, 则降级为通用列表收集器.
         *
         * @param tag 待追加的标签.
         * @return 当前收集器, 或降级后的收集器.
         */
        @Override
        public ListCollector accept(Tag tag) {
            if (tag instanceof ByteTag byteTag) {
                this.values.add(byteTag.getAsByte());
                return this;
            }
            // 类型不匹配, 降级为通用收集器
            return new GenericListCollector(this.values).accept(tag);
        }

        /**
         * 返回构建完成的字节数组标签.
         *
         * @return {@link ByteArrayTag} 结果.
         */
        @Override
        public Tag result() {
            return new ByteArrayTag(this.values.toByteArray());
        }
    }

    /**
     * Int 类型化列表收集器.
     * 若接收到非 {@link IntTag} 元素, 自动降级为 {@link GenericListCollector}.
     */
    static class IntListCollector implements ListCollector {
        private final IntArrayList values = new IntArrayList();

        /**
         * 使用已有 int 数组初始化收集器.
         *
         * @param values 初始 int 数组.
         */
        IntListCollector(int[] values) {
            this.values.addElements(0, values);
        }

        /**
         * 追加一个标签.
         * 若标签不是 {@link IntTag}, 则降级为通用列表收集器.
         *
         * @param tag 待追加的标签.
         * @return 当前收集器, 或降级后的收集器.
         */
        @Override
        public ListCollector accept(Tag tag) {
            if (tag instanceof IntTag intTag) {
                this.values.add(intTag.getAsInt());
                return this;
            }
            // 类型不匹配, 降级为通用收集器
            return new GenericListCollector(this.values).accept(tag);
        }

        /**
         * 返回构建完成的整型数组标签.
         *
         * @return {@link IntArrayTag} 结果.
         */
        @Override
        public Tag result() {
            return new IntArrayTag(this.values.toIntArray());
        }
    }

    /**
     * Long 类型化列表收集器.
     * 若接收到非 {@link LongTag} 元素, 自动降级为 {@link GenericListCollector}.
     */
    static class LongListCollector implements ListCollector {
        private final LongArrayList values = new LongArrayList();

        /**
         * 使用已有 long 数组初始化收集器.
         *
         * @param values 初始 long 数组.
         */
        LongListCollector(long[] values) {
            this.values.addElements(0, values);
        }

        /**
         * 追加一个标签.
         * 若标签不是 {@link LongTag}, 则降级为通用列表收集器.
         *
         * @param tag 待追加的标签.
         * @return 当前收集器, 或降级后的收集器.
         */
        @Override
        public ListCollector accept(Tag tag) {
            if (tag instanceof LongTag longTag) {
                this.values.add(longTag.getAsLong());
                return this;
            }
            // 类型不匹配, 降级为通用收集器
            return new GenericListCollector(this.values).accept(tag);
        }

        /**
         * 返回构建完成的长整型数组标签.
         *
         * @return {@link LongArrayTag} 结果.
         */
        @Override
        public Tag result() {
            return new LongArrayTag(this.values.toLongArray());
        }
    }

    /**
     * NBT 专用的 {@link RecordBuilder} 实现.
     * 以 {@link CompoundTag} 作为内部构建器, 逐步添加键值对, 最终输出完整的 CompoundTag.
     */
    class NbtRecordBuilder extends RecordBuilder.AbstractStringBuilder<Tag, CompoundTag> {
        /**
         * 创建一个基于 {@link NbtOps} 的记录构建器.
         */
        protected NbtRecordBuilder() {
            super(NbtOps.this);
        }

        /**
         * 初始化内部构建器.
         *
         * @return 空的 {@link CompoundTag}.
         */
        @Override
        protected CompoundTag initBuilder() {
            return new CompoundTag();
        }

        /**
         * 向构建中的 {@link CompoundTag} 追加一个键值对.
         *
         * @param key 键名.
         * @param value 值标签.
         * @param tag 当前构建器状态.
         * @return 追加后的构建器状态.
         */
        @Override
        protected CompoundTag append(String key, Tag value, CompoundTag tag) {
            tag.put(key, value);
            return tag;
        }

        /**
         * 将已构建的条目与前缀标签合并, 生成最终结果.
         * <ul>
         *   <li>tag 为 null 或 EndTag: 直接返回已构建的 CompoundTag</li>
         *   <li>tag 为 CompoundTag: 复制 tag 后, 将已构建的条目覆盖写入</li>
         *   <li>其他类型: 返回错误</li>
         * </ul>
         *
         * @param compoundTag 已构建完成的条目集合.
         * @param tag 需要合并的前缀标签.
         * @return 构建后的最终结果, 或错误结果.
         */
        @Override
        protected DataResult<Tag> build(CompoundTag compoundTag, Tag tag) {
            if (tag == null || tag == EndTag.INSTANCE) {
                return DataResult.success(compoundTag);
            }
            if (!(tag instanceof CompoundTag prefixCompound)) {
                return DataResult.error(() -> "mergeToMap called with not a map: " + tag, tag);
            }
            CompoundTag merged = prefixCompound.copy();
            for (Map.Entry<String, Tag> entry : compoundTag.entrySet()) {
                merged.put(entry.getKey(), entry.getValue());
            }
            return DataResult.success(merged);
        }
    }
}
