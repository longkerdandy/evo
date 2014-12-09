package com.github.longkerdandy.evo.tcp.codec;

import com.github.longkerdandy.evo.api.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.IOException;

import static com.github.longkerdandy.evo.api.util.JsonUtils.OBJECT_MAPPER;

/**
 * Packet Encoder
 * Encode Message into tcp packet.
 * Packet has binary header and json payload.
 * Header is 2 bytes ~ 5 bytes long, which compatible with MQTT header.
 * Byte 1 is not used.
 * Bytes 2 ~ 5 represent json payload length.
 * Payload is json and will be parsed into Message, passed to Handler.
 * ---------------------------------------
 * | Bit | 7 | 6 | 5 | 4 | 3 | 2 | 1 | 0 |
 * ---------------------------------------
 * | 1   | MQTT Type     | MQTT Flags    |
 * ---------------------------------------
 * | 2-5 | Remaining Length              |
 * ---------------------------------------
 * |       Message (JSON)                |
 * ---------------------------------------
 */
@SuppressWarnings("unused")
public class Encoder extends MessageToByteEncoder<Message> {

    /**
     * The Remaining Length is the number of bytes remaining within the current packet, including data in the
     * 258 variable header and the payload. The Remaining Length does not include the bytes used to encode the
     * 259 Remaining Length.
     * See MQTT V3.1.1 Protocol Specific for more information
     */
    protected static void encodeRemainingLength(ByteBuf out, int remainingLength) {
        do {
            int digit = remainingLength % 128;
            remainingLength /= 128;
            if (remainingLength > 0) {
                digit |= 0x80;
            }
            out.writeByte(digit);
        } while (remainingLength > 0);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        // message -> json
        byte[] bytes;
        try {
            bytes = OBJECT_MAPPER.writeValueAsBytes(msg);
        } catch (IOException e) {
            throw new EncoderException(e);
        }

        // header
        int remainingLength = bytes.length;
        if (remainingLength > Message.MAX_BYTES) {
            throw new EncoderException("too large message: " + remainingLength + " bytes");
        }
        out.writeByte(0);
        encodeRemainingLength(out, remainingLength);

        // write message
        out.writeBytes(bytes);
    }
}
