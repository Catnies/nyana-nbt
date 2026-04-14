package net.nyana.nbt.tag;

import net.nyana.nbt.tag.visitor.TagVisitor;

import java.io.DataOutput;
import java.io.IOException;

public class IntTag extends NumericTag {
    private static final int SELF_SIZE_IN_BYTES = 12;
    private final int value;

    public IntTag(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeInt(this.value);
    }

    @Override
    public byte getId() {
        return TAG_INT;
    }

    @Override
    public TagType<?> getType() {
        return TagTypes.INT;
    }

    @Override
    public IntTag deepClone() {
        return new IntTag(value);
    }

    @Override
    public int sizeInBytes() {
        return SELF_SIZE_IN_BYTES;
    }

    @Override
    public IntTag copy() {
        return this;
    }

    @Override
    public void accept(TagVisitor visitor) {
        visitor.visitInt(this);
    }

    @Override
    public long getAsLong() {
        return value;
    }

    @Override
    public int getAsInt() {
        return value;
    }

    @Override
    public short getAsShort() {
        return (short) (this.value & 65535);
    }

    @Override
    public byte getAsByte() {
        return (byte) (this.value & 255);
    }

    @Override
    public double getAsDouble() {
        return value;
    }

    @Override
    public float getAsFloat() {
        return value;
    }

    @Override
    public Number getAsNumber() {
        return value;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IntTag intTag)) return false;
        return value == intTag.value;
    }

    @Override
    public int hashCode() {
        return value;
    }
}
