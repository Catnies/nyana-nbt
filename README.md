# Nyana NBT

Nyana NBT 是一个面向 Java 21 的 NBT (Named Binary Tag) 读写库，提供核心 Tag 数据结构、二进制序列化工具，以及面向 Mojang DataFixerUpper 的 `DynamicOps<Tag>` 适配。

## 功能概览

- 支持标准 NBT 类型：`EndTag`、数值 Tag、`StringTag`、`ByteArrayTag`、`IntArrayTag`、`LongArrayTag`、`ListTag`、`CompoundTag`
- 通过 `NBT` 工具类创建标签、读写 `DataInput` / `DataOutput`、文件和字节数组
- `CompoundTag` 提供 `putXxx` / `getXxx` 便捷方法，并支持默认值读取
- `CompoundTag` 支持通过 `String[]` 路径读取深层值
- `UUID` 默认以 `IntArrayTag` 存储
- `Tag#getAsString()` 提供紧凑字符串表示，另有 pretty / compact visitor
- `ListTag` 支持异构元素；序列化时会在需要时使用 `CompoundTag` 包裹以兼容 NBT 列表格式
- `NbtOps` 可在 DFU `Codec<T>` 与 Nyana NBT `Tag` 之间转换

## 项目结构

```text
.
├── tag/                 # 核心 NBT Tag 类型与 NBT 工具类
├── codec/               # DataFixerUpper DynamicOps<Tag> 适配
├── agent-skill/         # AI Agent 技能与 API 参考
├── gradle/              # Gradle Wrapper
├── build.gradle.kts     # 根项目 Java 21、测试、发布配置
└── settings.gradle.kts  # Gradle 子项目声明
```

## 快速开始

创建、读写 `CompoundTag`：

```java
import net.nyana.nbt.NBT;
import net.nyana.nbt.tag.CompoundTag;
import net.nyana.nbt.tag.ListTag;
import net.nyana.nbt.tag.StringTag;

import java.io.File;
import java.util.UUID;

public class Example {
    public static void main(String[] args) throws Exception {
        CompoundTag root = NBT.createCompound();
        root.putString("name", "nyana");
        root.putInt("level", 42);
        root.putBoolean("enabled", true);
        root.putUUID("owner", UUID.randomUUID());

        ListTag items = NBT.createList();
        items.add(new StringTag("stone"));
        items.add(new StringTag("diamond"));
        root.put("items", items);

        byte[] bytes = NBT.toBytes(root);
        CompoundTag decoded = NBT.fromBytes(bytes);

        String name = decoded.getString("name", "unknown");
        int level = decoded.getInt("level", 0);

        NBT.writeFile(new File("data.nbt"), root);
        CompoundTag fromFile = NBT.readFile(new File("data.nbt"));
    }
}
```

读取嵌套路径：

```java
CompoundTag player = NBT.createCompound();
CompoundTag stats = NBT.createCompound();
stats.putInt("kills", 12);
player.put("stats", stats);

int kills = player.getInt(new String[]{"stats", "kills"}, 0);
```

使用 DataFixerUpper：

```java
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.nyana.nbt.codec.NbtOps;
import net.nyana.nbt.tag.Tag;

DataResult<Tag> encoded = Codec.INT.encodeStart(NbtOps.INSTANCE, 42);
Tag tag = encoded.result().orElseThrow();

DataResult<Integer> decoded = Codec.INT.parse(NbtOps.INSTANCE, tag);
int value = decoded.result().orElseThrow();
```

处理 `DataResult` 时应检查 `error()` 或 `result()`，不要假设编解码一定成功。


## API 参考

更详细的 API 摘要可见：

- [`agent-skill/references/api.md`](agent-skill/references/api.md)

## 安装

```kotlin
repositories {
    maven("https://repo.catnies.top/releases/")
}

dependencies {
    implementation("net.nyana:nyana-nbt-tag:1.0.0")
    implementation("net.nyana:nyana-nbt-codec:1.0.0") // For DataFixerUpper 8.0+
}
```