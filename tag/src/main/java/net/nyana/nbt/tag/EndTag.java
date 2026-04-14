package net.nyana.nbt.tag;

import net.nyana.nbt.tag.visitor.TagVisitor;

import java.io.DataOutput;

public class EndTag implements Tag {
    public static final int SELF_SIZE_IN_BYTES = 8;
    public static final EndTag INSTANCE = new EndTag();

    @Override
    public byte getId() {
        return TAG_END;
    }

    @Override
    public TagType<?> getType() {
        return TagTypes.END;
    }

    @Override
    public void write(DataOutput output) {
    }

    @Override
    public Tag copy() {
        return this;
    }

    @Override
    public EndTag deepClone() {
        return new EndTag();
    }

    @Override
    public int sizeInBytes() {
        return SELF_SIZE_IN_BYTES;
    }

    @Override
    public void accept(TagVisitor visitor) {
        visitor.visitEnd(this);
    }

    @Override
    public String toString() {
        return this.getAsString();
    }
}
