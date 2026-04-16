package net.nyana.nbt.tag;

import net.nyana.nbt.tag.visitor.TagVisitor;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class StringTag implements Tag {
    private static final int SELF_SIZE_IN_BYTES = 36;
    private static final StringTag EMPTY = new StringTag("");
    private final String value;

    public StringTag(@NotNull String value) {
        this.value = value;
    }

    public static void skipString(@NotNull DataInput input) throws IOException {
        input.skipBytes(input.readUnsignedShort());
    }

    public @NotNull String value() {
        return value;
    }

    @Override
    public byte getId() {
        return TAG_STRING;
    }

    @Override
    public @NotNull TagType<?> getType() {
        return TagTypes.STRING;
    }

    @Override
    public void write(@NotNull DataOutput output) throws IOException {
        output.writeUTF(this.value);
    }

    @Override
    public @NotNull Tag copy() {
        return this;
    }

    @Override
    public @NotNull Tag deepClone() {
        return new StringTag(this.value);
    }

    @Override
    public int sizeInBytes() {
        return SELF_SIZE_IN_BYTES + 2 * this.value.length();
    }

    @Override
    public void accept(@NotNull TagVisitor visitor) {
        visitor.visitString(this);
    }

    /**
     * 转义给定字符串中的特殊字符并将结果括在引号中.
     * 函数动态判断是使用单引号('')还是双引号("")
     * 基于字符串的内容，根据需要转义引号和反斜杠。
     *
     * @param value 要引用和转义的输入字符串
     * @return 转义并加引号的字符串
     */
    public static @NotNull String quoteAndEscape(@NotNull String value) {
        StringBuilder stringBuilder = new StringBuilder(" ");
        char quoteChar = 0;
        for (int i = 0; i < value.length(); ++i) {
            char currentChar = value.charAt(i);
            if (currentChar == '\\') {
                stringBuilder.append('\\');
            } else if (currentChar == '"' || currentChar == '\'') {
                if (quoteChar == 0) {
                    quoteChar = currentChar == '"' ? '\'' : '"';
                }
                if (quoteChar == currentChar) {
                    stringBuilder.append('\\');
                }
            }
            stringBuilder.append(currentChar);
        }
        if (quoteChar == 0) {
            quoteChar = '"';
        }
        stringBuilder.setCharAt(0, quoteChar);
        stringBuilder.append(quoteChar);
        return stringBuilder.toString();
    }

    @Override
    public @NotNull String toString() {
        return Tag.super.getAsString();
    }

    @Override
    public @NotNull String getAsString() {
        return this.value;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StringTag stringTag)) return false;
        return this.value.equals(stringTag.value);
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }
}
