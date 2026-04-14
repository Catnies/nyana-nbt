package net.nyana.nbt.codec;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.nyana.nbt.tag.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NbtOpsTest {

    @Test
    void convertToJson_shouldConvertPrimitiveAndArrayTags() {
        JsonElement intJson = NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, new IntTag(42));
        JsonElement stringJson = NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, new StringTag("nyana"));
        JsonElement byteArrayJson = NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, new ByteArrayTag(new byte[]{1, 2, 3}));

        Assertions.assertTrue(intJson.isJsonPrimitive());
        Assertions.assertEquals(42, intJson.getAsInt());
        Assertions.assertEquals("nyana", stringJson.getAsString());
        Assertions.assertTrue(byteArrayJson.isJsonArray());
        Assertions.assertEquals(3, byteArrayJson.getAsJsonArray().size());
        Assertions.assertEquals(1, byteArrayJson.getAsJsonArray().get(0).getAsInt());
    }

    @Test
    void convertToJson_shouldConvertCompoundAndListTags() {
        ListTag listTag = new ListTag();
        listTag.add(new StringTag("a"));
        listTag.add(new StringTag("b"));

        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put("name", new StringTag("flower"));
        compoundTag.put("level", new IntTag(3));
        compoundTag.put("items", listTag);

        JsonElement converted = NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, compoundTag);

        Assertions.assertTrue(converted.isJsonObject());
        JsonObject object = converted.getAsJsonObject();
        Assertions.assertEquals("flower", object.get("name").getAsString());
        Assertions.assertEquals(3, object.get("level").getAsInt());
        JsonArray items = object.getAsJsonArray("items");
        Assertions.assertEquals(2, items.size());
        Assertions.assertEquals("a", items.get(0).getAsString());
    }

    @Test
    void mergeToMap_shouldReturnMergedCopyWhenInputIsValid() {
        CompoundTag original = new CompoundTag();
        original.put("keep", new IntTag(1));

        DataResult<Tag> mergedResult = NbtOps.INSTANCE.mergeToMap(original, new StringTag("add"), new IntTag(2));
        Tag merged = mergedResult.result().orElseThrow();

        Assertions.assertInstanceOf(CompoundTag.class, merged);
        CompoundTag mergedCompound = (CompoundTag) merged;
        Assertions.assertEquals(1, mergedCompound.getInt("keep"));
        Assertions.assertEquals(2, mergedCompound.getInt("add"));
        Assertions.assertFalse(original.containsKey("add"));
    }

    @Test
    void mergeToMap_shouldReturnErrorWhenKeyIsNotString() {
        DataResult<Tag> result = NbtOps.INSTANCE.mergeToMap(EndTag.INSTANCE, new IntTag(1), new StringTag("v"));

        Assertions.assertTrue(result.error().isPresent());
    }

    @Test
    void mergeToList_shouldKeepByteArrayWhenAppendingByte() {
        ByteArrayTag origin = new ByteArrayTag(new byte[]{1, 2});

        Tag merged = NbtOps.INSTANCE.mergeToList(origin, new ByteTag((byte) 3)).result().orElseThrow();

        Assertions.assertInstanceOf(ByteArrayTag.class, merged);
        Assertions.assertArrayEquals(new byte[]{1, 2, 3}, ((ByteArrayTag) merged).getAsByteArray());
    }

    @Test
    void mergeToList_shouldDowngradeToListTagWhenAppendingDifferentType() {
        ByteArrayTag origin = new ByteArrayTag(new byte[]{1});

        Tag merged = NbtOps.INSTANCE.mergeToList(origin, new IntTag(2)).result().orElseThrow();

        Assertions.assertInstanceOf(ListTag.class, merged);
        ListTag listTag = (ListTag) merged;
        Assertions.assertEquals(2, listTag.size());
        Assertions.assertEquals(Tag.TAG_BYTE, listTag.get(0).getId());
        Assertions.assertEquals(Tag.TAG_INT, listTag.get(1).getId());
    }

    @Test
    void remove_shouldReturnNewCompoundWithoutTargetKey() {
        CompoundTag map = new CompoundTag();
        map.put("keep", new IntTag(1));
        map.put("removeMe", new IntTag(2));

        Tag removed = NbtOps.INSTANCE.remove(map, "removeMe");

        Assertions.assertInstanceOf(CompoundTag.class, removed);
        CompoundTag removedMap = (CompoundTag) removed;
        Assertions.assertFalse(removedMap.containsKey("removeMe"));
        Assertions.assertTrue(removedMap.containsKey("keep"));
        Assertions.assertTrue(map.containsKey("removeMe"));
        Assertions.assertNotSame(map, removedMap);
    }
}
