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
 */
public class Encoder extends MessageToByteEncoder<Message> {

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
        out.writeByte(0); // skip 1st byte
        do {
            int digit = remainingLength % 128;
            remainingLength /= 128;
            if (remainingLength > 0) {
                digit |= 0x80;
            }
            out.writeByte(digit);
        } while (remainingLength > 0);

        // write message
        out.writeBytes(bytes);
    }
}
