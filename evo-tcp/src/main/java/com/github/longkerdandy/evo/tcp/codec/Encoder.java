package com.github.longkerdandy.evo.tcp.codec;

import com.github.longkerdandy.evo.api.message.Message;
import com.github.longkerdandy.evo.api.protocol.Const;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.IOException;

import static com.github.longkerdandy.evo.api.util.JsonUtils.ObjectMapper;

/**
 * Packet Encoder
 * Encode Message into tcp packet.
 * Packet has binary header and payload.
 * Header is 7 bytes ~ 10 bytes long.
 * Bytes 1 ~ 3 signature .
 * Byte 4 is protocol version.
 * Byte 5 is protocol type.
 * Byte 6 is reserved at the moment.
 * Bytes 7 ~ 10 represent payload length.
 * Payload is payload and will be parsed into Message, passed to Handler.
 * ---------------------------------------
 * | Bit | 7 | 6 | 5 | 4 | 3 | 2 | 1 | 0 |
 * ---------------------------------------
 * | 1-3 | Signature                     |
 * ---------------------------------------
 * | 4   | Protocol Version              |
 * ---------------------------------------
 * | 5   | Protocol Type                 |
 * ---------------------------------------
 * | 6   | Reserved                      |
 * ---------------------------------------
 * | 7-10| Remaining Length              |
 * ---------------------------------------
 * |       Message Data                  |
 * ---------------------------------------
 */
@SuppressWarnings("unused")
public class Encoder extends MessageToByteEncoder<Message> {

    /**
     * The Remaining Length is the number of bytes remaining within the current packet, including data in the payload.
     * The Remaining Length does not include the bytes used to encode the Remaining Length.
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
        // prepare message data
        byte[] bytes;
        try {
            bytes = ObjectMapper.writeValueAsBytes(msg);
        } catch (IOException e) {
            throw new EncoderException(e);
        }

        // header
        int remainingLength = bytes.length;
        if (remainingLength > Const.MESSAGE_MAX_BYTES) {
            throw new EncoderException("Message size is too large: " + remainingLength + " bytes");
        }
        out.writeByte(0x45);
        out.writeByte(0x56);
        out.writeByte(0x4F);
        out.writeByte((short) msg.getPv());
        out.writeByte((short) msg.getPt());
        out.writeByte(0);
        encodeRemainingLength(out, remainingLength);

        // write message data
        out.writeBytes(bytes);
    }
}
