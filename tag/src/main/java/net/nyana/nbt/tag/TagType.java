package net.nyana.nbt.tag;

import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.IOException;

public interface TagType<T extends Tag> {

    /**
     * 返回此标签类型的名称.
     *
     * @return 标签类型的名称
     */
    @NotNull String name();

    /**
     * 在输入流中跳过指定数量的标签.
     *
     * @param input 要跳过数据的输入流
     * @param count 要跳过的标签数量
     * @throws IOException 如果发生 I/O 错误
     */
    void skip(@NotNull DataInput input, int count) throws IOException;

    /**
     * 在输入流中跳过单个标签.
     *
     * @param input 要跳过数据的输入流
     * @throws IOException 如果发生 I/O 错误
     */
    void skip(@NotNull DataInput input) throws IOException;

    /**
     * 以指定深度从输入流中读取一个标签.
     *
     * @param input 要读取数据的输入流
     * @param depth 当前标签结构的深度（用于避免无限递归）
     * @return 从输入流中读取到的标签
     * @throws IOException 如果发生 I/O 错误
     */
    @NotNull T read(@NotNull DataInput input, int depth) throws IOException;

    /**
     * 检查此标记类型是否表示一个值.
     *
     * @return 如果此标记类型表示值, 则为 true, 否则为 false
     */
    default boolean isValue() {
        return false;
    }

    /**
     * 固定大小标签类型的接口.
     *
     * @param <T> 与此 TagType 关联的具体 Tag 类型
     */
    interface FixedSize<T extends Tag> extends TagType<T> {

        /**
         * 返回此标签类型的固定大小（以字节为单位）.
         *
         * @return 字节数
         */
        int size();

        /**
         * 通过在输入流中跳过固定字节数来跳过单个标签.
         *
         * @param input 要跳过数据的输入流
         * @throws IOException 如果发生 I/O 错误
         */
        @Override
        default void skip(@NotNull DataInput input) throws IOException {
            input.skipBytes(this.size());
        }

        /**
         * 通过在输入流中跳过固定字节数来跳过指定数量的标签.
         *
         * @param input 要跳过数据的输入流
         * @param count 要跳过的标签数量
         * @throws IOException 如果发生 I/O 错误
         */
        @Override
        default void skip(@NotNull DataInput input, int count) throws IOException {
            input.skipBytes(this.size() * count);
        }
    }

    /**
     * 可变大小标签类型的接口.
     *
     * @param <T> 与此 TagType 关联的具体 Tag 类型
     */
    interface FlexibleSize<T extends Tag> extends TagType<T> {

        /**
         * 通过反复调用 skip 方法来跳过指定数量的标签.
         *
         * @param input 要跳过数据的输入流
         * @param count 要跳过的标签数量
         * @throws IOException 如果发生 I/O 错误
         */
        @Override
        default void skip(@NotNull DataInput input, int count) throws IOException {
            for (int i = 0; i < count; ++i) {
                this.skip(input);
            }
        }
    }
}
