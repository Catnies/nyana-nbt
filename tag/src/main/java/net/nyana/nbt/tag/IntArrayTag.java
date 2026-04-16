package net.nyana.nbt.tag;

import net.nyana.nbt.tag.visitor.TagVisitor;
import net.nyana.nbt.util.ArrayUtil;
import net.nyana.nbt.util.UUIDUtil;import org.jetbrains.annotations.NotNull;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

public class IntArrayTag extends CollectionTag<IntTag> {
    private static final int SELF_SIZE_IN_BYTES = 24;
    private int[] data;

    public IntArrayTag(int[] data) {
        this.data = data;
    }

    public int[] getAsIntArray() {
        return this.data;
    }

    public int[] value() {
        return data;
    }

    public @NotNull UUID getAsUUID() {
        if (this.data.length != 4) {
            throw new IllegalArgumentException("Failed to convert IntArray into UUID because the length of the array is " + data.length + " which is expected to be 4.");
        } else {
            return UUIDUtil.uuidFromIntArray(this.data);
        }
    }

    @Override
    public @NotNull IntTag get(int index) {
        return new IntTag(this.data[index]);
    }

    @Override
    public @NotNull IntTag set(int index, @NotNull IntTag tag) {
        int j = this.data[index];
        this.data[index] = tag.getAsInt();
        return new IntTag(j);
    }

    @Override
    public void add(int index, @NotNull IntTag tag) {
        this.data = ArrayUtil.add(this.data, index, tag.getAsInt());
    }

    @Override
    public @NotNull IntTag remove(int index) {
        int j = this.data[index];
        this.data = ArrayUtil.remove(this.data, index);
        return new IntTag(j);
    }

    @Override
    public void clear() {
        this.data = new int[0];
    }

    @Override
    public boolean isEmpty() {
        return this.data.length == 0;
    }

    @Override
    public boolean setTag(int index, @NotNull Tag tag) {
        if (tag instanceof NumericTag) {
            this.data[index] = ((NumericTag) tag).getAsInt();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean addTag(int index, @NotNull Tag tag) {
        if (tag instanceof NumericTag) {
            this.data = ArrayUtil.add(this.data, index, ((NumericTag) tag).getAsInt());
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
    public void write(@NotNull DataOutput output) throws IOException {
        output.writeInt(this.data.length);
        for (int k : this.data) {
            output.writeInt(k);
        }
    }

    @Override
    public byte getId() {
        return TAG_INT_ARRAY;
    }

    @Override
    public @NotNull TagType<?> getType() {
        return TagTypes.INT;
    }

    @Override
    public @NotNull Tag deepClone() {
        return new IntArrayTag(this.data.clone());
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
        visitor.visitIntArray(this);
    }

    @Override
    public @NotNull String toString() {
        return this.getAsString();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IntArrayTag intTags)) return false;
        return Arrays.equals(data, intTags.data);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }
}
