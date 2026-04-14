package net.nyana.nbt.tag;

import net.nyana.nbt.tag.visitor.TagVisitor;
import net.nyana.nbt.util.MathUtil;

import java.io.DataOutput;
import java.io.IOException;

public class FloatTag extends NumericTag {
    private static final int SELF_SIZE_IN_BYTES = 12;
    public static final FloatTag ZERO = new FloatTag(0.0F);
    private final float value;

    public FloatTag(float value) {
        this.value = value;
    }

    public float value() {
        return value;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeFloat(this.value);
    }

    @Override
    public byte getId() {
        return TAG_FLOAT;
    }

    @Override
    public TagType<?> getType() {
        return TagTypes.FLOAT;
    }

    @Override
    public FloatTag deepClone() {
        return new FloatTag(this.value);
    }

    @Override
    public int sizeInBytes() {
        return SELF_SIZE_IN_BYTES;
    }

    @Override
    public FloatTag copy() {
        return this;
    }

    @Override
    public void accept(TagVisitor visitor) {
        visitor.visitFloat(this);
    }

    @Override
    public long getAsLong() {
        return (long) this.value;
    }

    @Override
    public int getAsInt() {
        return MathUtil.fastFloor(this.value);
    }

    @Override
    public short getAsShort() {
        return (short) (MathUtil.fastFloor(this.value) & 65535);
    }

    @Override
    public byte getAsByte() {
        return (byte) (MathUtil.fastFloor(this.value) & 255);
    }

    @Override
    public double getAsDouble() {
        return this.value;
    }

    @Override
    public float getAsFloat() {
        return this.value;
    }

    @Override
    public Number getAsNumber() {
        return this.value;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FloatTag floatTag)) return false;
        return Float.compare(value, floatTag.value) == 0;
    }

    @Override
    public int hashCode() {
        return Float.hashCode(value);
    }
}
