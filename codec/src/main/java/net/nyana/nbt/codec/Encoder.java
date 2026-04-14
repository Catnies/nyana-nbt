package net.nyana.nbt.codec;

@FunctionalInterface
public interface Encoder<O, T> {

    void encode(O output, T value);

}
