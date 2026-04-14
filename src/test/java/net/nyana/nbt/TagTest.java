package net.nyana.nbt;

import com.mojang.serialization.DataResult;
import net.nyana.nbt.codec.NbtOps;
import net.nyana.nbt.tag.*;
import net.nyana.nbt.tag.visitor.PrettyStringTagVisitor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TagTest {

    @Test
    public void createCompoundTag() {
        DataResult<Tag> build = NbtOps.INSTANCE.mapBuilder()
                .add("integer", new IntTag(10))
                .add("string", new StringTag("flower"))
                .add("compound", NbtOps.INSTANCE.mapBuilder().add("byte", new ByteTag((byte) 0)).build(EndTag.INSTANCE))
                .build(EndTag.INSTANCE);

        Tag tag = build.result().orElse(null);
        Assertions.assertNotNull(tag);

        System.out.println(new PrettyStringTagVisitor().visit(tag));
    }

}
