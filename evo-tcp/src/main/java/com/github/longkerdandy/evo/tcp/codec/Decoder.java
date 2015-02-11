package com.github.longkerdandy.evo.tcp.codec;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.longkerdandy.evo.api.message.Message;
import com.github.longkerdandy.evo.api.protocol.MessageSize;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;

import java.io.IOException;
import java.util.List;

import static com.github.longkerdandy.evo.api.util.JsonUtils.ObjectMapper;

/**
 * Packet Decoder
 * Decode tcp packet into Message.
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
public class Decoder extends ByteToMessageDecoder {

    /**
     * The Remaining Length is the number of bytes remaining within the current packet, including data in the
     * 258 variable header and the payload. The Remaining Length does not include the bytes used to encode the
     * 259 Remaining Length.
     * See MQTT V3.1.1 Protocol Specific for more information
     */
    protected static int decodeRemainingLength(ByteBuf in) {
        int remainingLength = 0;
        int multiplier = 1;
        short digit;
        int loops = 0;
        do {
            digit = in.readUnsignedByte();
            remainingLength += (digit & 127) * multiplier;
            multiplier *= 128;
            loops++;
        } while ((digit & 128) != 0 && loops < 4);
        if (loops == 4 && (digit & 128) != 0) {
            in.clear();
            throw new DecoderException("remaining length exceeds 4 digits");
        }
        return remainingLength;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // at least 2 bytes for the header
        if (!in.isReadable() || in.readableBytes() < 2) return;

        in.markReaderIndex();

        // header
        short b1 = in.readUnsignedByte();
        int messageType = b1 >> 4;
        if (messageType != 0 && messageType != 15) {
            in.clear();
            throw new DecoderException("MQTT message type: " + messageType + " received");
        }
        int remainingLength = decodeRemainingLength(in);
        if (remainingLength > MessageSize.MAX_BYTES) {
            in.clear();
            throw new DecoderException("too large message: " + remainingLength + " bytes");
        }

        // need more data?
        if (in.readableBytes() < remainingLength) {
            in.resetReaderIndex();
            return;
        }

        // json -> message
        try {
            JavaType type = ObjectMapper.getTypeFactory().constructParametricType(Message.class, JsonNode.class);
            Message<JsonNode> msg = ObjectMapper.readValue(new ByteBufInputStream(in, remainingLength), type);
            out.add(msg);
        } catch (IOException e) {
            in.clear();
            throw new DecoderException(e);
        }
    }
}
