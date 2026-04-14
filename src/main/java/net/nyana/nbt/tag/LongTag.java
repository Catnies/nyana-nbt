package net.nyana.nbt.tag;

import net.nyana.nbt.tag.visitor.TagVisitor;

import java.io.DataOutput;
import java.io.IOException;

public class LongTag extends NumericTag {
    private static final int SELF_SIZE_IN_BYTES = 16;
    private final long value;

    public LongTag(final long value) {
        this.value = value;
    }

    public long value() {
        return value;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeLong(this.value);
    }

    @Override
    public byte getId() {
        return TAG_LONG;
    }

    @Override
    public TagType<?> getType() {
        return TagTypes.LONG;
    }

    @Override
    public LongTag deepClone() {
        return new LongTag(this.value);
    }

    @Override
    public int sizeInBytes() {
        return SELF_SIZE_IN_BYTES;
    }

    @Override
    public LongTag copy() {
        return this;
    }

    @Override
    public void accept(TagVisitor visitor) {
        visitor.visitLong(this);
    }

    @Override
    public long getAsLong() {
        return this.value;
    }

    @Override
    public int getAsInt() {
        return (int) this.value;
    }

    @Override
    public short getAsShort() {
        return (short) ((int) (this.value & 65535L));
    }

    @Override
    public byte getAsByte() {
        return (byte) ((int) (this.value & 255L));
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
        if (!(o instanceof LongTag longTag)) return false;
        return value == longTag.value;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(value);
    }
}
