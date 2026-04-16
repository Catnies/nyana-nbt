package net.nyana.nbt.tag;

import net.nyana.nbt.tag.visitor.TagVisitor;
import org.jetbrains.annotations.NotNull;

import java.io.DataOutput;
import java.io.IOException;

public class ByteTag extends NumericTag {
    private static final int SELF_SIZE_IN_BYTES = 9;
    public static final ByteTag ZERO = new ByteTag((byte) 0);
    public static final ByteTag ONE = new ByteTag((byte) 1);
    private final byte value;

    public ByteTag(byte value) {
        this.value = value;
    }

    public ByteTag(boolean b) {
        this.value = (byte) (b ? 1 : 0);
    }

    public byte value() {
        return value;
    }

    public boolean booleanValue() {
        return this.value != 0;
    }

    @Override
    public byte getId() {
        return TAG_BYTE;
    }

    @Override
    public @NotNull TagType<?> getType() {
        return TagTypes.BYTE;
    }

    @Override
    public void write(@NotNull DataOutput output) throws IOException {
        output.writeByte(this.value);
    }

    @Override
    public @NotNull Tag copy() {
        return this;
    }

    @Override
    public @NotNull Tag deepClone() {
        return new ByteTag(this.value);
    }

    @Override
    public int sizeInBytes() {
        return SELF_SIZE_IN_BYTES;
    }

    @Override
    public void accept(@NotNull TagVisitor visitor) {
        visitor.visitByte(this);
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
        return this.value;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ByteTag byteTag)) return false;
        return value == byteTag.value;
    }

    @Override
    public int hashCode() {
        return value;
    }
}
