package com.github.longkerdandy.evo.api.netty;

import com.github.longkerdandy.evo.api.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;

import java.io.IOException;
import java.util.List;

/**
 * Packet Decoder
 * Decode tcp packet into Message.
 * Packet has binary header and payload.
 * Header is 7 bytes ~ 10 bytes long.
 * Bytes 1 ~ 3 signature .
 * Byte 4 is protocol.
 * Byte 5 is reserved at the moment.
 * Byte 6 is reserved at the moment.
 * Bytes 7 ~ 10 represent payload length.
 * Message Data will be parsed into Message, passed to Handler.
 * ---------------------------------------
 * | Bit | 7 | 6 | 5 | 4 | 3 | 2 | 1 | 0 |
 * ---------------------------------------
 * | 1-3 | Signature                     |
 * ---------------------------------------
 * | 4   | Protocol                      |
 * ---------------------------------------
 * | 5   | Reserved                      |
 * ---------------------------------------
 * | 6   | Reserved                      |
 * ---------------------------------------
 * | 7-10| Remaining Length              |
 * ---------------------------------------
 * |       Message Data                  |
 * ---------------------------------------
 */
public class Decoder extends ByteToMessageDecoder {

    /**
     * The Remaining Length is the number of bytes remaining within the current packet, including data in the payload.
     * The Remaining Length does not include the bytes used to encode the Remaining Length.
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
            throw new DecoderException("remaining length exceeds 4 bytes");
        }
        return remainingLength;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // at least 7 bytes for the header
        if (!in.isReadable() || in.readableBytes() < 7) return;

        in.markReaderIndex();

        // header 1-3 signature
        short b1 = in.readUnsignedByte();
        short b2 = in.readUnsignedByte();
        short b3 = in.readUnsignedByte();
        if (b1 != 0x45 || b2 != 0x56 || b3 != 0x4F) {
            in.clear();
            throw new DecoderException("Wrong message signature");
        }
        // header 4 protocol type
        short b4 = in.readUnsignedByte();
        // header 5 reserved
        in.readUnsignedByte();
        // header 6 reserved
        in.readUnsignedByte();
        // header 7-10 remaining length
        int remainingLength = decodeRemainingLength(in);
        if (remainingLength > Message.MAX_BYTES) {
            in.clear();
            throw new DecoderException("Message size is too large: " + remainingLength + " bytes");
        }

        // need more data?
        if (in.readableBytes() < remainingLength) {
            in.resetReaderIndex();
            return;
        }

        // message data
        try {
            // parse json
            Message msg = Message.parseMessage(new ByteBufInputStream(in, remainingLength));
            msg.setProtocol(b4);    // save protocol from header to message
            if (msg.getTimestamp() <= 0) msg.setTimestamp(System.currentTimeMillis());  // if timestamp not provided
            // validate
            msg.validate();
            // pass to handler
            out.add(msg);
        } catch (IOException | IllegalStateException e) {
            in.clear();
            throw new DecoderException(e);
        }
    }
}
