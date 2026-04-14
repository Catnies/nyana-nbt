package net.nyana.nbt.codec;

public interface Codec<B, V> extends Encoder<B, V>, Decoder<B, V> {

    static <B, V> Codec<B, V> of(final Encoder<B, V> encoder, final Decoder<B, V> decoder) {
        return new Codec<>() {
            @Override
            public V decode(final B input) {
                return decoder.decode(input);
            }

            @Override
            public void encode(final B output, final V value) {
                encoder.encode(output, value);
            }
        };
    }

}
