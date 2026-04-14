package net.nyana.nbt.tag;

import java.util.AbstractList;

/**
 * 表示 NBT 元素集合的抽象类.
 * 为类列表的 NBT 结构 (如 ListTag) 提供基础实现.
 *
 * @param <T> 此集合所持有的标签类型
 */
public abstract class CollectionTag<T extends Tag> extends AbstractList<T> implements Tag {

    /**
     * 返回集合中指定索引处的标签.
     *
     * @param index 要返回的标签的索引
     * @return 指定索引处的标签
     * @throws IndexOutOfBoundsException 如果索引超出范围
     *         (index < 0 || index >= size())
     */
    @Override
    public abstract T get(int index);

    /**
     * 将指定索引处的标签替换为给定的标签.
     *
     * @param index 要替换的标签的索引
     * @param tag   要设置在指定索引处的新标签
     * @return 之前位于指定索引处的标签
     */
    @Override
    public abstract T set(int index, T tag);

    /**
     * 在集合的指定索引处插入指定标签.
     * 将当前位于该位置的标签 (如有) 及其后续标签向右移动.
     *
     * @param index 要插入标签的索引
     * @param tag   要插入的标签
     */
    @Override
    public abstract void add(int index, T tag);

    /**
     * 从集合中移除指定索引处的标签.
     * 将后续标签向左移动.
     *
     * @param index 要移除的标签的索引
     * @return 从集合中移除的标签
     */
    @Override
    public abstract T remove(int index);

    /**
     * 将指定索引处的标签替换为给定的标签.
     * 与 {@link #set} 不同, 此方法允许任意 {@link Tag} 类型.
     *
     * @param index 要替换的标签的索引
     * @param tag   要设置在指定索引处的新标签
     * @return 如果标签成功替换则返回 true, 否则返回 false
     */
    public abstract boolean setTag(int index, Tag tag);

    /**
     * 在集合的指定索引处插入指定标签.
     * 与 {@link #add} 不同, 此方法允许任意 {@link Tag} 类型.
     *
     * @param index 要插入标签的索引
     * @param tag   要插入的标签
     * @return 如果标签成功添加则返回 true, 否则返回 false
     */
    public abstract boolean addTag(int index, Tag tag);
}
