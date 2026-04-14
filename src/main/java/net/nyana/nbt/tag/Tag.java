package net.nyana.nbt.tag;

import net.nyana.nbt.tag.visitor.CompactStringTagVisitor;
import net.nyana.nbt.tag.visitor.TagVisitor;

import java.io.DataOutput;
import java.io.IOException;

public interface Tag {

    byte TAG_END = 0;
    byte TAG_BYTE = 1;
    byte TAG_SHORT = 2;
    byte TAG_INT = 3;
    byte TAG_LONG = 4;
    byte TAG_FLOAT = 5;
    byte TAG_DOUBLE = 6;
    byte TAG_BYTE_ARRAY = 7;
    byte TAG_STRING = 8;
    byte TAG_LIST = 9;
    byte TAG_COMPOUND = 10;
    byte TAG_INT_ARRAY = 11;
    byte TAG_LONG_ARRAY = 12;
    byte TAG_ANY_NUMERIC = 99;

    /**
     * 返回该标签类型的ID.
     *
     * @return 代表标签类型ID的字节
     */
    byte getId();

    /**
     * 检索该标签的TagType.
     *
     * @return 与此标签关联的特定 TagType 实例
     */
    TagType<?> getType();

    /**
     * 将此标签的二进制表示写入指定的输出流.
     *
     * @param output 要写入的数据输出流
     * @throws IOException 如果写入时发生 I/O 错误
     */
    void write(DataOutput output) throws IOException;

    /**
     * 创建此标签的浅拷贝.
     *
     * @return 与当前标签内容相同的新 Tag 实例
     */
    Tag copy();

    /**
     * 创建此标签的深度克隆, 包括所有嵌套元素.
     *
     * @return 一个新的 Tag 实例
     */
    Tag deepClone();

    /**
     * 当前Tag进行序列化后, 占用的字节数量.
     *
     * @return 占用的字节数
     */
    int sizeInBytes();

    /**
     * 接受一个访问者来处理此标签.
     *
     * @param visitor 用于处理此标签的访问者实例
     */
    void accept(TagVisitor visitor);

    /**
     * 检查此标签是否为指定类型, 或是否匹配通用数值类型.
     *
     * @param type 要检查的类型 ID
     * @return 若标签匹配指定类型则返回 true, 否则返回 false
     */
    default boolean isTypeOf(int type) {
        int i = getId();
        if (i == type) {
            return true;
        } else if (type != TAG_ANY_NUMERIC) {
            return false;
        } else {
            return isNumericTag(i);
        }
    }

    /**
     * 返回此标签的紧凑字符串表示形式.
     *
     * @return 以紧凑格式表示该标签的字符串
     */
    default String getAsString() {
        return (new CompactStringTagVisitor()).visit(this);
    }

    /**
     * 返回标签的字符串表示形式.
     *
     * @return 标签的字符串表示
     */
    @Override
    String toString();

    /**
     * 判断给定的标签类型 ID 是否对应数值类型.
     *
     * @param tagType 要检查的标签类型 ID
     * @return 若该标签类型为数值类型则返回 true, 否则返回 false
     */
    static boolean isNumericTag(int tagType) {
        return switch (tagType) {
            case TAG_BYTE, TAG_SHORT, TAG_INT, TAG_LONG, TAG_FLOAT, TAG_DOUBLE -> true;
            default -> false;
        };
    }
}
