package net.nyana.nbt.tag;

import net.nyana.nbt.tag.visitor.TagVisitor;

import java.io.DataOutput;
import java.io.IOException;

public class ShortTag extends NumericTag {
    private static final int SELF_SIZE_IN_BYTES = 10;
    private final short value;

    public ShortTag(short value) {
        this.value = value;
    }

    @Override
    public byte getId() {
        return TAG_SHORT;
    }

    @Override
    public TagType<?> getType() {
        return TagTypes.SHORT;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeShort(this.value);
    }

    @Override
    public Tag copy() {
        return this;
    }

    @Override
    public Tag deepClone() {
        return new ShortTag(this.value);
    }

    @Override
    public int sizeInBytes() {
        return SELF_SIZE_IN_BYTES;
    }

    @Override
    public void accept(TagVisitor visitor) {
        visitor.visitShort(this);
    }

    @Override
    public long getAsLong() {
        return this.value;
    }

    @Override
    public int getAsInt() {
        return this.value;
    }

    @Override
    public short getAsShort() {
        return this.value;
    }

    @Override
    public byte getAsByte() {
        return (byte) (this.value & 255);
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
        if (!(o instanceof ShortTag shortTag)) return false;
        return value == shortTag.value;
    }

    @Override
    public int hashCode() {
        return value;
    }
}
