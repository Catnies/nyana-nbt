package net.nyana.nbt.tag;

import org.jetbrains.annotations.NotNull;import java.io.DataInput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TagTypes {

    public static final TagType<EndTag> END = new TagType<>() {
        @Override
        public @NotNull String name() {
            return "End";
        }

        @Override
        public void skip(@NotNull DataInput input, int count) {
        }

        @Override
        public void skip(@NotNull DataInput input) {
        }

        @Override
        public @NotNull EndTag read(@NotNull DataInput input, int depth) {
            return new EndTag();
        }
    };

    public static final TagType<ByteTag> BYTE = new TagType.FixedSize<>() {

        @Override
        public @NotNull String name() {
            return "Byte";
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public @NotNull ByteTag read(@NotNull DataInput input, int depth) throws IOException {
            return new ByteTag(input.readByte());
        }

        @Override
        public boolean isValue() {
            return true;
        }
    };

    public static final TagType<ShortTag> SHORT = new TagType.FixedSize<>() {
        @Override
        public @NotNull ShortTag read(@NotNull DataInput dataInput, int depth) throws IOException {
            return new ShortTag(dataInput.readShort());
        }

        @Override
        public int size() {
            return 2;
        }

        @Override
        public @NotNull String name() {
            return "Short";
        }

        @Override
        public boolean isValue() {
            return true;
        }
    };

    public static final TagType<IntTag> INT = new TagType.FixedSize<>() {
        @Override
        public @NotNull IntTag read(@NotNull DataInput dataInput, int depth) throws IOException {
            return new IntTag(dataInput.readInt());
        }

        @Override
        public int size() {
            return 4;
        }

        @Override
        public @NotNull String name() {
            return "Int";
        }

        @Override
        public boolean isValue() {
            return true;
        }
    };

    public static final TagType<LongTag> LONG = new TagType.FixedSize<>() {
        @Override
        public @NotNull LongTag read(@NotNull DataInput dataInput, int depth) throws IOException {
            return new LongTag(dataInput.readLong());
        }

        @Override
        public int size() {
            return 8;
        }

        @Override
        public @NotNull String name() {
            return "Long";
        }

        @Override
        public boolean isValue() {
            return true;
        }
    };

    public static final TagType<FloatTag> FLOAT = new TagType.FixedSize<>() {
        @Override
        public @NotNull FloatTag read(@NotNull DataInput dataInput, int depth) throws IOException {
            return new FloatTag(dataInput.readFloat());
        }

        @Override
        public int size() {
            return 4;
        }

        @Override
        public @NotNull String name() {
            return "Float";
        }

        @Override
        public boolean isValue() {
            return true;
        }
    };

    public static final TagType<DoubleTag> DOUBLE = new TagType.FixedSize<>() {
        @Override
        public @NotNull DoubleTag read(@NotNull DataInput dataInput, int depth) throws IOException {
            return new DoubleTag(dataInput.readDouble());
        }

        @Override
        public int size() {
            return 8;
        }

        @Override
        public @NotNull String name() {
            return "Double";
        }

        @Override
        public boolean isValue() {
            return true;
        }
    };

    public static final TagType<ByteArrayTag> BYTE_ARRAY = new TagType.FlexibleSize<>() {
        @Override
        public @NotNull ByteArrayTag read(@NotNull DataInput input, int depth) throws IOException {
            int length = input.readInt();
            byte[] array = new byte[length];
            input.readFully(array);
            return new ByteArrayTag(array);
        }

        @Override
        public void skip(@NotNull DataInput input) throws IOException {
            input.skipBytes(input.readInt());
        }

        @Override
        public @NotNull String name() {
            return "Byte[]";
        }
    };

    public static final TagType<StringTag> STRING = new TagType.FlexibleSize<>() {
        @Override
        public @NotNull StringTag read(@NotNull DataInput dataInput, int depth) throws IOException {
            String string = dataInput.readUTF();
            return new StringTag(string);
        }

        @Override
        public void skip(@NotNull DataInput input) throws IOException {
            StringTag.skipString(input);
        }

        @Override
        public @NotNull String name() {
            return "String";
        }

        @Override
        public boolean isValue() {
            return true;
        }
    };

    public static final TagType<ListTag> LIST = new TagType.FlexibleSize<>() {
        @Override
        public @NotNull ListTag read(@NotNull DataInput dataInput, int depth) throws IOException {
            if (depth > 512) {
                throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
            } else {
                byte typeId = dataInput.readByte();
                int length = dataInput.readInt();
                if (typeId == 0 && length > 0) {
                    throw new RuntimeException("Missing type on ListTag");
                } else {
                    TagType<?> tagType = TagTypes.typeById(typeId);
                    ListTag listTag = new ListTag();
                    for (int k = 0; k < length; ++k) {
                        listTag.addAndUnwrap(tagType.read(dataInput, depth + 1));
                    }
                    return listTag;
                }
            }
        }

        @Override
        public void skip(@NotNull DataInput input) throws IOException {
            TagType<?> tagType = TagTypes.typeById(input.readByte());
            int i = input.readInt();
            tagType.skip(input, i);
        }

        @Override
        public@NotNull String name() {
            return "List";
        }
    };

    public static final TagType<CompoundTag> COMPOUND = new TagType.FlexibleSize<>() {
        @Override
        public @NotNull CompoundTag read(@NotNull DataInput dataInput, int depth) throws IOException {
            if (depth > 512) {
                throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
            } else {
                Map<String, Tag> map = new HashMap<>(8, 0.8f);
                byte typeId;
                while ((typeId = CompoundTag.readNamedTagType(dataInput)) != Tag.TAG_END) {
                    String string = CompoundTag.readNamedTagName(dataInput);
                    Tag tag = CompoundTag.readNamedTagData(TagTypes.typeById(typeId), dataInput, depth + 1);
                    map.put(string, tag);
                }
                return new CompoundTag(map);
            }
        }

        @Override
        public void skip(@NotNull DataInput input) throws IOException {
            byte typeId;
            while ((typeId = input.readByte()) != Tag.TAG_END) {
                StringTag.skipString(input);
                TagTypes.typeById(typeId).skip(input);
            }
        }

        @Override
        public @NotNull String name() {
            return "Compound";
        }
    };

    public static final TagType<IntArrayTag> INT_ARRAY = new TagType.FlexibleSize<>() {
        @Override
        public @NotNull IntArrayTag read(@NotNull DataInput input, int depth) throws IOException {
            int length = input.readInt();
            int[] array = new int[length];
            for (int k = 0; k < length; ++k) {
                array[k] = input.readInt();
            }
            return new IntArrayTag(array);
        }

        @Override
        public void skip(@NotNull DataInput input) throws IOException {
            input.skipBytes(input.readInt() * 4);
        }

        @Override
        public @NotNull String name() {
            return "Int[]";
        }
    };

    public static final TagType<LongArrayTag> LONG_ARRAY = new TagType.FlexibleSize<>() {
        @Override
        public @NotNull LongArrayTag read(@NotNull DataInput dataInput, int depth) throws IOException {
            int length = dataInput.readInt();
            long[] array = new long[length];
            for (int k = 0; k < length; ++k) {
                array[k] = dataInput.readLong();
            }
            return new LongArrayTag(array);
        }

        @Override
        public void skip(@NotNull DataInput input) throws IOException {
            input.skipBytes(input.readInt() * 8);
        }

        @Override
        public @NotNull String name() {
            return "Long[]";
        }
    };


    private static final TagType<?>[] TYPES = new TagType[] {
            END, BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, BYTE_ARRAY, STRING, LIST, COMPOUND, INT_ARRAY, LONG_ARRAY
    };

    /**
     * 检索与给定标签 ID 关联的 TagType.
     *
     * @param id TagType 的 ID
     * @return 给定 ID 对应的 TagType
     * @throws ArrayIndexOutOfBoundsException 如果ID超出有效范围
     */
    public static @NotNull TagType<?> typeById(int id) {
        return TYPES[id];
    }
}
