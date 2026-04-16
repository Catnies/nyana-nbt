package net.nyana.nbt.tag;

import net.nyana.nbt.tag.visitor.TagVisitor;
import net.nyana.nbt.util.ArrayUtil;
import org.jetbrains.annotations.NotNull;

import java.io.DataOutput;
import java.io.IOException;

public class ByteArrayTag extends CollectionTag<ByteTag> {
    private static final int SELF_SIZE_IN_BYTES = 24;
    private byte[] data;

    public ByteArrayTag(final byte[] data) {
        this.data = data;
    }

    public byte[] getAsByteArray() {
        return this.data;
    }

    @Override
    public byte getId() {
        return TAG_BYTE_ARRAY;
    }

    @Override
    public @NotNull TagType<?> getType() {
        return TagTypes.BYTE_ARRAY;
    }

    @Override
    public void write(@NotNull DataOutput output) throws IOException {
        output.writeInt(this.data.length);
        output.write(this.data);
    }

    @Override
    public @NotNull Tag copy() {
        return deepClone();
    }

    @Override
    public @NotNull Tag deepClone() {
        return new ByteArrayTag(this.data.clone());
    }

    @Override
    public int sizeInBytes() {
        return SELF_SIZE_IN_BYTES + size();
    }

    @Override
    public void accept(@NotNull TagVisitor visitor) {
        visitor.visitByteArray(this);
    }

    @Override
    public @NotNull ByteTag set(int index, @NotNull ByteTag tag) {
        byte b0 = this.data[index];
        this.data[index] = tag.getAsByte();
        return new ByteTag(b0);
    }

    @Override
    public void add(int index, @NotNull ByteTag tag) {
        this.data = ArrayUtil.add(this.data, index, tag.getAsByte());
    }

    @Override
    public @NotNull ByteTag remove(int index) {
        byte b0 = this.data[index];
        this.data = ArrayUtil.remove(this.data, index);
        return new ByteTag(b0);
    }

    @Override
    public @NotNull ByteTag get(int index) {
        return new ByteTag(this.data[index]);
    }

    @Override
    public boolean setTag(int index, @NotNull Tag tag) {
        if (tag instanceof NumericTag) {
            this.data[index] = ((NumericTag) tag).getAsByte();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean addTag(int index, @NotNull Tag tag) {
        if (tag instanceof NumericTag) {
            this.data = ArrayUtil.add(this.data, index, ((NumericTag) tag).getAsByte());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int size() {
        return this.data.length;
    }
}
