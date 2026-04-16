package net.nyana.nbt.tag;

import net.nyana.nbt.tag.visitor.TagVisitor;
import org.jetbrains.annotations.NotNull;

import java.io.DataOutput;

public class EndTag implements Tag {
    public static final int SELF_SIZE_IN_BYTES = 8;
    public static final EndTag INSTANCE = new EndTag();

    @Override
    public byte getId() {
        return TAG_END;
    }

    @Override
    public @NotNull TagType<?> getType() {
        return TagTypes.END;
    }

    @Override
    public void write(@NotNull DataOutput output) {
    }

    @Override
    public @NotNull Tag copy() {
        return this;
    }

    @Override
    public @NotNull Tag deepClone() {
        return new EndTag();
    }

    @Override
    public int sizeInBytes() {
        return SELF_SIZE_IN_BYTES;
    }

    @Override
    public void accept(@NotNull TagVisitor visitor) {
        visitor.visitEnd(this);
    }

    @Override
    public @NotNull String toString() {
        return this.getAsString();
    }
}
