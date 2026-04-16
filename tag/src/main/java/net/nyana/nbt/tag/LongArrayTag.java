package net.nyana.nbt.tag;

import net.nyana.nbt.tag.visitor.TagVisitor;
import net.nyana.nbt.util.ArrayUtil;
import org.jetbrains.annotations.NotNull;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class LongArrayTag extends CollectionTag<LongTag> {
    private static final int SELF_SIZE_IN_BYTES = 24;
    private long[] data;

    public LongArrayTag(long[] data) {
        this.data = data;
    }

    @Override
    public @NotNull LongTag get(int index) {
        return new LongTag(data[index]);
    }

    @Override
    public @NotNull LongTag set(int index, @NotNull LongTag tag) {
        long l = this.data[index];
        this.data[index] = tag.getAsLong();
        return new LongTag(l);
    }

    @Override
    public void add(int index, @NotNull LongTag tag) {
        this.data = ArrayUtil.add(this.data, index, tag.getAsLong());
    }

    @Override
    public @NotNull LongTag remove(int index) {
        long l = this.data[index];
        this.data = ArrayUtil.remove(this.data, index);
        return new LongTag(l);
    }

    @Override
    public void clear() {
        this.data = new long[0];
    }

    @Override
    public boolean isEmpty() {
        return this.data.length == 0;
    }

    @Override
    public boolean setTag(int index, @NotNull Tag tag) {
        if (tag instanceof NumericTag) {
            this.data[index] = ((NumericTag) tag).getAsLong();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean addTag(int index, @NotNull Tag tag) {
        if (tag instanceof NumericTag) {
            this.data = ArrayUtil.add(this.data, index, ((NumericTag) tag).getAsLong());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int size() {
        return this.data.length;
    }

    @Override
    public byte getId() {
        return TAG_LONG_ARRAY;
    }

    @Override
    public void write(@NotNull DataOutput output) throws IOException {
        output.writeInt(this.data.length);
        for(long l : this.data) {
            output.writeLong(l);
        }
    }

    @Override
    public@NotNull TagType<?> getType() {
        return TagTypes.LONG_ARRAY;
    }

    @Override
    public @NotNull Tag deepClone() {
        return new LongArrayTag(this.data.clone());
    }

    @Override
    public int sizeInBytes() {
        return SELF_SIZE_IN_BYTES + 4 * this.data.length;
    }

    @Override
    public @NotNull Tag copy() {
        return deepClone();
    }

    @Override
    public void accept(@NotNull TagVisitor visitor) {
        visitor.visitLongArray(this);
    }

    public long[] getAsLongArray() {
        return this.data;
    }

    public long[] value() {
        return data;
    }

    @Override
    public @NotNull String toString() {
        return this.getAsString();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LongArrayTag longTags)) return false;
        return Arrays.equals(data, longTags.data);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }
}
