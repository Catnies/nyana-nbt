package net.nyana.nbt;

import net.nyana.nbt.tag.*;
import net.nyana.nbt.util.UUIDUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 用于创建和处理 NBT (Named Binary Tag) 对象的工具类.
 * 提供创建, 读取, 写入和转换 NBT 标签的方法.
 */
public class NBT {

    private NBT() {}

    public static ByteTag createByte(byte b) {
        return new ByteTag(b);
    }

    public static ByteTag createBoolean(boolean b) {
        return new ByteTag(b);
    }

    public static ShortTag createShort(short s) {
        return new ShortTag(s);
    }

    public static IntTag createInt(int i) {
        return new IntTag(i);
    }

    public static LongTag createLong(long l) {
        return new LongTag(l);
    }

    public static FloatTag createFloat(float f) {
        return new FloatTag(f);
    }

    public static DoubleTag createDouble(double d) {
        return new DoubleTag(d);
    }

    public static StringTag createString(String s) {
        return new StringTag(s);
    }

    public static IntArrayTag createIntArray(int[] a) {
        return new IntArrayTag(a);
    }

    public static IntArrayTag createUUID(UUID uuid) {
        return new IntArrayTag(UUIDUtil.uuidToIntArray(uuid));
    }

    public static ByteArrayTag createByteArray(byte[] b) {
        return new ByteArrayTag(b);
    }

    public static LongArrayTag createLongArray(long[] a) {
        return new LongArrayTag(a);
    }

    public static CompoundTag createCompound(Map<String, Tag> tags) {
        return new CompoundTag(tags);
    }

    public static CompoundTag createCompound() {
        return new CompoundTag();
    }

    public static ListTag createList() {
        return new ListTag();
    }

    public static ListTag createList(List<Tag> tags) {
        return new ListTag(tags);
    }

    /**
     * 从 DataInput 流中读取一个无名 NBT 标签.
     *
     * @param input 要读取的输入流
     * @return 读取到的 NBT 标签
     * @throws IOException 如果发生 I/O 错误
     */
    public static Tag readUnnamedTag(DataInput input, boolean named) throws IOException {
        byte typeId = input.readByte();
        if (typeId == 0) {
            return EndTag.INSTANCE;
        } else {
            if (named) {
                StringTag.skipString(input);
            }
            try {
                return TagTypes.typeById(typeId).read(input, 0);
            } catch (IOException ioException) {
                throw new IOException(ioException);
            }
        }
    }

    /**
     * 将一个有名或无名的 NBT 标签写入 DataOutput 流.
     *
     * @param tag    要写入的标签
     * @param output 要写入的输出流
     * @throws IOException 如果发生 I/O 错误
     */
    public static void writeUnnamedTag(Tag tag, DataOutput output, boolean named) throws IOException {
        output.writeByte(tag.getId());
        if (tag.getId() != Tag.TAG_END) {
            if (named) {
                output.writeUTF("");
            }
            tag.write(output);
        }
    }

    /**
     * 从 DataInput 流中读取一个 CompoundTag.
     *
     * @param input 要读取的输入流
     * @return 读取到的 CompoundTag
     * @throws IOException 如果发生 I/O 错误, 或根标签不是 CompoundTag
     */
    public static CompoundTag readCompound(DataInput input, boolean named) throws IOException {
        Tag tag = readUnnamedTag(input, named);
        if (tag instanceof CompoundTag) {
            return (CompoundTag) tag;
        } else {
            throw new IOException("Root tag must be CompoundTag");
        }
    }

    /**
     * 将一个 CompoundTag 写入 DataOutput 流.
     *
     * @param nbt    要写入的 CompoundTag
     * @param output 要写入的输出流
     * @throws IOException 如果发生 I/O 错误
     */
    public static void writeCompound(CompoundTag nbt, DataOutput output, boolean named) throws IOException {
        writeUnnamedTag(nbt, output, named);
    }

    /**
     * 从文件中读取一个 CompoundTag.
     *
     * @param file 要读取的文件
     * @return 读取到的 CompoundTag, 若文件不存在或为空则返回 null
     * @throws IOException 如果发生 I/O 错误
     */
    @Nullable
    public static CompoundTag readFile(File file) throws IOException {
        if (!file.exists()) {
            return null;
        }
        if (file.length() == 0) {
            return null;
        }
        try (FileInputStream fis = new FileInputStream(file);
             DataInputStream input = new DataInputStream(fis)) {
            return readCompound(input, false);
        }
    }

    /**
     * 将一个 CompoundTag 写入文件.
     *
     * @param file 要写入的文件
     * @param nbt  要写入的 CompoundTag
     * @throws IOException 如果发生 I/O 错误
     */
    public static void writeFile(File file, CompoundTag nbt) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(file);
             DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream)) {
            writeCompound(nbt, dataOutputStream, false);
        }
    }

    /**
     * 将字节数组转换为 CompoundTag.
     *
     * @param bytes 要转换的字节数组
     * @return 对应的 CompoundTag, 若字节数组为 null 或为空则返回 null
     * @throws IOException 如果发生 I/O 错误
     */
    @Nullable
    public static CompoundTag fromBytes(byte[] bytes) throws IOException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream)) {
            return readCompound(dataInputStream, false);
        }
    }

    /**
     * 将 CompoundTag 转换为字节数组.
     *
     * @param nbt 要转换的 CompoundTag
     * @return 表示该 CompoundTag 的字节数组
     * @throws IOException 如果发生 I/O 错误
     */
    public static byte @NotNull [] toBytes(@NotNull CompoundTag nbt) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)) {
            writeCompound(nbt, dataOutputStream, false);
            return byteArrayOutputStream.toByteArray();
        }
    }

    /**
     * 将标签转换为字节数组.
     *
     * @param nbt 要转换的标签
     * @return 表示该标签的字节数组
     * @throws IOException 如果发生 I/O 错误
     */
    public static byte @NotNull [] toBytes(@NotNull Tag nbt, boolean named) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)) {
            writeUnnamedTag(nbt, dataOutputStream, named);
            return byteArrayOutputStream.toByteArray();
        }
    }
}
