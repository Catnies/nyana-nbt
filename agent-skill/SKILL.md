---
name: nyana-nbt
description: Nyana NBT Java 库使用指南。用于需要在 Java/Kotlin 项目中读写 NBT CompoundTag、Tag、ByteArrayTag、UUID、字节数组持久化数据，或需要用 Nyana NBT 作为数据库 bytes/blob 字段序列化格式时触发；也用于需要接入 net.nyana 的 nyana/nayana-nbt-tag、nyana/nayana-nbt-codec 依赖和 DataFixerUpper Codec 适配时。
---

# Nyana NBT

## 使用原则

- 优先使用 `net.nyana.nbt.NBT` 的静态工厂和读写方法，不要手写 NBT 二进制格式。
- 数据库 `bytes`/`blob` 字段建议存储 `NBT.toBytes(compoundTag)` 的结果；读取时用 `NBT.fromBytes(bytes)`。
- 需要复杂结构时以 `CompoundTag` 为根节点，内部使用 `putString`、`putLong`、`putUUID`、`putByteArray`、`put` 等方法。
- 不确定依赖坐标时先看仓库源码的 `build.gradle.kts`，不要只信 README。当前仓库 README 和发布配置存在 artifactId 拼写差异。

## 依赖接入

仓库地址：[Catnies/nyana-nbt](https://github.com/Catnies/nyana-nbt)

仓库 README 写法：

```kotlin
repositories {
    maven("https://repo.catnies.top/releases/")
}

dependencies {
    implementation("net.nyana:nyana-nbt-tag:1.0.0")
    implementation("net.nyana:nyana-nbt-codec:0.15")
}
```
实际开发时先用 Gradle 刷新依赖验证哪个坐标可解析；如果项目已有私服镜像，也检查 `https://repo.catnies.top/releases/` 中的实际 artifact 名。

## 常用写法

### 构建详情数据并转 bytes

```kotlin
import net.nyana.nbt.NBT

val detail = NBT.createCompound()
detail.putString("action", "bind")
detail.putUUID("item", itemUuid)
detail.putUUID("player", playerUuid)
detail.putLong("time", System.currentTimeMillis())
detail.putString("reason", "shift_drop")

val bytes = NBT.toBytes(detail)
```

### 从 bytes 读取详情数据

```kotlin
import net.nyana.nbt.NBT

val detail = NBT.fromBytes(bytes) ?: NBT.createCompound()
val action = detail.getString("action")
val item = detail.getUUID("item")
val player = detail.getUUID("player")
val time = detail.getLong("time")
val reason = detail.getString("reason")
```

### 嵌套 Compound

```kotlin
val root = NBT.createCompound()
val extra = NBT.createCompound()
extra.putString("world", worldName)
extra.putDouble("x", x)
extra.putDouble("y", y)
extra.putDouble("z", z)
root.put("extra", extra)

val loadedExtra = root.getCompound("extra")
```

### ByteArray 字段

```kotlin
val root = NBT.createCompound()
root.putByteArray("raw", rawBytes)

val raw = root.getByteArray("raw") ?: byteArrayOf()
```

## DataFixerUpper Codec

需要把 DFU `Codec<T>` 和 Nyana NBT 互转时使用 `net.nyana.nbt.codec.NbtOps.INSTANCE`。详细方法和限制见 [references/api.md](references/api.md)。

## 校验

- 修改项目后运行 Gradle 构建确认依赖可解析。
- 写入数据库 bytes 前确认根节点是 `CompoundTag`。
- 读取 bytes 时处理 `NBT.fromBytes(bytes)` 返回 `null` 的情况。
- 捕获或上抛 `IOException`，不要吞掉 NBT 读写错误。
