# Nyana NBT API 参考

来源：`Catnies/nyana-nbt` master 分支源码。

## 模块

- `tag`：核心 NBT 标签体系，包名 `net.nyana.nbt` / `net.nyana.nbt.tag`
- `codec`：DataFixerUpper `DynamicOps<Tag>` 适配，包名 `net.nyana.nbt.codec`

## NBT 工具类

核心类：`net.nyana.nbt.NBT`

创建标签：

```java
NBT.createByte(byte)
NBT.createBoolean(boolean)
NBT.createShort(short)
NBT.createInt(int)
NBT.createLong(long)
NBT.createFloat(float)
NBT.createDouble(double)
NBT.createString(String)
NBT.createUUID(UUID)
NBT.createByteArray(byte[])
NBT.createIntArray(int[])
NBT.createLongArray(long[])
NBT.createCompound()
NBT.createCompound(Map<String, Tag>)
NBT.createList()
NBT.createList(List<Tag>)
```

读写二进制：

```java
NBT.readUnnamedTag(DataInput input, boolean named)
NBT.writeUnnamedTag(Tag tag, DataOutput output, boolean named)
NBT.readCompound(DataInput input, boolean named)
NBT.writeCompound(CompoundTag nbt, DataOutput output, boolean named)
NBT.fromBytes(byte[] bytes)
NBT.toBytes(CompoundTag nbt)
NBT.toBytes(Tag nbt, boolean named)
NBT.readFile(File file)
NBT.writeFile(File file, CompoundTag nbt)
```

注意：

- `fromBytes(null)` 或空数组返回 `null`
- `readCompound` 要求根标签是 `CompoundTag`
- `toBytes(CompoundTag)` 使用 unnamed 根标签写法

## CompoundTag

类：`net.nyana.nbt.tag.CompoundTag`

写入：

```java
put(String key, Tag tag)
putBoolean(String key, boolean value)
putByte(String key, byte value)
putShort(String key, short value)
putInt(String key, int value)
putLong(String key, long value)
putFloat(String key, float value)
putDouble(String key, double value)
putString(String key, String value)
putByteArray(String key, byte[] value)
putIntArray(String key, int[] value)
putLongArray(String key, long[] value)
putUUID(String key, UUID value)
```

读取：

```java
get(String key)
getBoolean(String key, boolean defaultValue)
getByte(String key, byte defaultValue)
getShort(String key, short defaultValue)
getInt(String key, int defaultValue)
getLong(String key, long defaultValue)
getFloat(String key, float defaultValue)
getDouble(String key, double defaultValue)
getString(String key, String defaultValue)
getByteArray(String key, byte[] defaultValue)
getIntArray(String key, int[] defaultValue)
getLongArray(String key, long[] defaultValue)
getUUID(String key, UUID defaultValue)
getCompound(String key, CompoundTag defaultValue)
getList(String key, ListTag defaultValue)
```

路径读取：

```java
getString(new String[]{"outer", "inner", "key"})
getCompound(new String[]{"outer", "inner"})
getUUID(new String[]{"item", "owner"})
```

其它：

```java
containsKey(String key)
remove(String key)
keySet()
entrySet()
copy()
deepClone()
size()
isEmpty()
```

## ByteArrayTag

类：`net.nyana.nbt.tag.ByteArrayTag`

```java
new ByteArrayTag(byte[])
getAsByteArray()
size()
copy()
deepClone()
```

## UUID 存储

`CompoundTag.putUUID(key, uuid)` 内部使用 `IntArrayTag` 存储 UUID。读取用：

```java
val uuid = compound.getUUID("key")
```

如果需要和外部 JSON/字符串协议兼容，也可以用 `putString("uuid", uuid.toString())`。

## NbtOps

类：`net.nyana.nbt.codec.NbtOps`

单例：

```java
NbtOps.INSTANCE
```

用途：

- 将 DFU `Codec<T>` 编码为 `Tag`
- 从 `Tag` 解码为对象
- 与 Mojang `DynamicOps` 体系互转

示意：

```kotlin
val resultTag = codec.encodeStart(NbtOps.INSTANCE, value)
val value = codec.parse(NbtOps.INSTANCE, tag)
```

处理 `DataResult` 时必须检查错误，不要直接假设成功。
