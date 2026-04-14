package net.nyana.nbt.tag;

public abstract class NumericTag implements Tag {

    protected NumericTag() {}

    /**
     * 将此标签的值作为 long 类型返回.
     *
     * @return long 类型的数值
     */
    public abstract long getAsLong();

    /**
     * 将此标签的值作为 int 类型返回.
     *
     * @return int 类型的数值
     */
    public abstract int getAsInt();

    /**
     * 将此标签的值作为 short 类型返回.
     *
     * @return short 类型的数值
     */
    public abstract short getAsShort();

    /**
     * 将此标签的值作为 byte 类型返回.
     *
     * @return byte 类型的数值
     */
    public abstract byte getAsByte();

    /**
     * 将此标签的值作为 double 类型返回.
     *
     * @return double 类型的数值
     */
    public abstract double getAsDouble();

    /**
     * 将此标签的值作为 float 类型返回.
     *
     * @return float 类型的数值
     */
    public abstract float getAsFloat();

    /**
     * 将此标签的值作为通用 Number 对象返回.
     * 该值以其最自然的数值类型表示。
     *
     * @return Number 类型的数值
     */
    public abstract Number getAsNumber();

    @Override
    public String toString() {
        return this.getAsString();
    }
}
