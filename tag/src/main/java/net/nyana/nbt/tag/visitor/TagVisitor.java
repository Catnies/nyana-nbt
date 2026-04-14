package net.nyana.nbt.tag.visitor;

import net.nyana.nbt.tag.*;

public interface TagVisitor {

    /**
     * 访问一个 ByteTag 元素。
     *
     * @param element 要访问的 ByteTag
     */
    void visitByte(ByteTag element);

    /**
     * 访问一个 ShortTag 元素。
     *
     * @param element 要访问的 ShortTag
     */
    void visitShort(ShortTag element);

    /**
     * 访问一个 IntTag 元素。
     *
     * @param element 要访问的 IntTag
     */
    void visitInt(IntTag element);

    /**
     * 访问一个 LongTag 元素。
     *
     * @param element 要访问的 LongTag
     */
    void visitLong(LongTag element);

    /**
     * 访问一个 FloatTag 元素。
     *
     * @param element 要访问的 FloatTag
     */
    void visitFloat(FloatTag element);

    /**
     * 访问一个 DoubleTag 元素。
     *
     * @param element 要访问的 DoubleTag
     */
    void visitDouble(DoubleTag element);

    /**
     * 访问一个 StringTag 元素。
     *
     * @param element 要访问的 StringTag
     */
    void visitString(StringTag element);

    /**
     * 访问一个 ByteArrayTag 元素。
     *
     * @param element 要访问的 ByteArrayTag
     */
    void visitByteArray(ByteArrayTag element);

    /**
     * 访问一个 IntArrayTag 元素。
     *
     * @param element 要访问的 IntArrayTag
     */
    void visitIntArray(IntArrayTag element);

    /**
     * 访问一个 LongArrayTag 元素。
     *
     * @param element 要访问的 LongArrayTag
     */
    void visitLongArray(LongArrayTag element);

    /**
     * 访问一个 ListTag 元素。
     *
     * @param element 要访问的 ListTag
     */
    void visitList(ListTag element);

    /**
     * 访问一个 CompoundTag 元素，表示一组键值对的集合。
     *
     * @param compound 要访问的 CompoundTag
     */
    void visitCompound(CompoundTag compound);

    /**
     * 访问一个 EndTag 元素，用于标志复合标签或列表标签的结束。
     *
     * @param element 要访问的 EndTag
     */
    void visitEnd(EndTag element);
}