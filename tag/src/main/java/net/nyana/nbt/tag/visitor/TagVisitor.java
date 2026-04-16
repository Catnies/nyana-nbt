package net.nyana.nbt.tag.visitor;

import net.nyana.nbt.tag.*;
import org.jetbrains.annotations.NotNull;

public interface TagVisitor {

    /**
     * 访问一个 ByteTag 元素。
     *
     * @param element 要访问的 ByteTag
     */
    void visitByte(@NotNull ByteTag element);

    /**
     * 访问一个 ShortTag 元素。
     *
     * @param element 要访问的 ShortTag
     */
    void visitShort(@NotNull ShortTag element);

    /**
     * 访问一个 IntTag 元素。
     *
     * @param element 要访问的 IntTag
     */
    void visitInt(@NotNull IntTag element);

    /**
     * 访问一个 LongTag 元素。
     *
     * @param element 要访问的 LongTag
     */
    void visitLong(@NotNull LongTag element);

    /**
     * 访问一个 FloatTag 元素。
     *
     * @param element 要访问的 FloatTag
     */
    void visitFloat(@NotNull FloatTag element);

    /**
     * 访问一个 DoubleTag 元素。
     *
     * @param element 要访问的 DoubleTag
     */
    void visitDouble(@NotNull DoubleTag element);

    /**
     * 访问一个 StringTag 元素。
     *
     * @param element 要访问的 StringTag
     */
    void visitString(@NotNull StringTag element);

    /**
     * 访问一个 ByteArrayTag 元素。
     *
     * @param element 要访问的 ByteArrayTag
     */
    void visitByteArray(@NotNull ByteArrayTag element);

    /**
     * 访问一个 IntArrayTag 元素。
     *
     * @param element 要访问的 IntArrayTag
     */
    void visitIntArray(@NotNull IntArrayTag element);

    /**
     * 访问一个 LongArrayTag 元素。
     *
     * @param element 要访问的 LongArrayTag
     */
    void visitLongArray(@NotNull LongArrayTag element);

    /**
     * 访问一个 ListTag 元素。
     *
     * @param element 要访问的 ListTag
     */
    void visitList(@NotNull ListTag element);

    /**
     * 访问一个 CompoundTag 元素，表示一组键值对的集合。
     *
     * @param compound 要访问的 CompoundTag
     */
    void visitCompound(@NotNull CompoundTag compound);

    /**
     * 访问一个 EndTag 元素，用于标志复合标签或列表标签的结束。
     *
     * @param element 要访问的 EndTag
     */
    void visitEnd(@NotNull EndTag element);
}