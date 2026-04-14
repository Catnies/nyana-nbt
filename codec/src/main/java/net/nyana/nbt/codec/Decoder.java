package net.nyana.nbt.codec;

@FunctionalInterface
public interface Decoder<I, T> {

    T decode(I input);

}
