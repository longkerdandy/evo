package com.github.longkerdandy.evo.tcp.codec;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.longkerdandy.evo.api.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;

import java.io.IOException;
import java.util.List;

import static com.github.longkerdandy.evo.api.util.JsonUtils.OBJECT_MAPPER;

/**
 * Packet Decoder
 */
@SuppressWarnings("unused")
public class Decoder extends ByteToMessageDecoder {

    private static final int MAX_BYTES_IN_MESSAGE = 8092;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // at least 2 bytes for the header
        if (!in.isReadable() || in.readableBytes() < 2) return;

        in.markReaderIndex();

        // header (inspired by MQTT 3.1.1)
        short b1 = in.readUnsignedByte();
        int messageType = b1 >> 4;
        boolean dupFlag = (b1 & 0x08) == 0x08;
        int qosLevel = (b1 & 0x06) >> 1;
        boolean retain = (b1 & 0x01) != 0;
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
            in.clear(); // is clear the correct way to discard this message?
            throw new DecoderException("remaining length exceeds 4 digits (" + messageType + ')');
        }
        if (remainingLength > MAX_BYTES_IN_MESSAGE) {
            in.clear(); // is clear the correct way to discard this message?
            throw new DecoderException("too large message: " + remainingLength + " bytes");
        }

        // enough data?
        if (in.readableBytes() < remainingLength) {
            in.resetReaderIndex();
            return;
        }

        // json -> message
        try {
            JavaType type = OBJECT_MAPPER.getTypeFactory().constructParametricType(Message.class, JsonNode.class);
            Message<JsonNode> msg = OBJECT_MAPPER.readValue(new ByteBufInputStream(in, remainingLength), type);
            out.add(msg);
        } catch (IOException e) {
            in.clear(); // is clear the correct way to discard this message?
            throw new DecoderException(e);
        }
    }
}
