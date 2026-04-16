package net.nyana.nbt.tag;

import net.nyana.nbt.tag.visitor.TagVisitor;
import org.jetbrains.annotations.NotNull;

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
    public void write(@NotNull DataOutput output) throws IOException {
        output.writeLong(this.value);
    }

    @Override
    public byte getId() {
        return TAG_LONG;
    }

    @Override
    public @NotNull TagType<?> getType() {
        return TagTypes.LONG;
    }

    @Override
    public @NotNull Tag deepClone() {
        return new LongTag(this.value);
    }

    @Override
    public int sizeInBytes() {
        return SELF_SIZE_IN_BYTES;
    }

    @Override
    public @NotNull Tag copy() {
        return this;
    }

    @Override
    public void accept(@NotNull TagVisitor visitor) {
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
    public @NotNull Number getAsNumber() {
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
