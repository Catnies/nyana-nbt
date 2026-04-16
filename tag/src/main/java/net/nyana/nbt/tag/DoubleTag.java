package net.nyana.nbt.tag;

import net.nyana.nbt.tag.visitor.TagVisitor;
import net.nyana.nbt.util.MathUtil;import org.jetbrains.annotations.NotNull;

import java.io.DataOutput;
import java.io.IOException;

public class DoubleTag extends NumericTag {
    private static final int SELF_SIZE_IN_BYTES = 16;
    public static final DoubleTag ZERO = new DoubleTag(0.0);
    private final double value;

    public DoubleTag(double value) {
        this.value = value;
    }

    public double value() {
        return value;
    }

    @Override
    public byte getId() {
        return TAG_DOUBLE;
    }

    @Override
    public @NotNull TagType<?> getType() {
        return TagTypes.DOUBLE;
    }

    @Override
    public void write(@NotNull DataOutput output) throws IOException {
        output.writeDouble(this.value);
    }

    @Override
    public @NotNull Tag copy() {
        return this;
    }

    @Override
    public @NotNull Tag deepClone() {
        return new DoubleTag(this.value);
    }

    @Override
    public int sizeInBytes() {
        return SELF_SIZE_IN_BYTES;
    }

    @Override
    public void accept(@NotNull TagVisitor visitor) {
        visitor.visitDouble(this);
    }

    @Override
    public long getAsLong() {
        return (long) Math.floor(this.value);
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
        return (float) this.value;
    }

    @Override
    public @NotNull Number getAsNumber() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DoubleTag doubleTag)) return false;
        return value == doubleTag.value;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(this.value);
    }
}
