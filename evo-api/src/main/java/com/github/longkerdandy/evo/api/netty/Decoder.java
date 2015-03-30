package com.github.longkerdandy.evo.api.netty;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.longkerdandy.evo.api.message.*;
import com.github.longkerdandy.evo.api.protocol.Const;
import com.github.longkerdandy.evo.api.protocol.MessageType;
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
 * Packet has binary header and payload.
 * Header is 7 bytes ~ 10 bytes long.
 * Bytes 1 ~ 3 signature .
 * Byte 4 is protocol version.
 * Byte 5 is reserved at the moment.
 * Byte 6 is reserved at the moment.
 * Bytes 7 ~ 10 represent payload length.
 * Message Data will be parsed into Message, passed to Handler.
 * ---------------------------------------
 * | Bit | 7 | 6 | 5 | 4 | 3 | 2 | 1 | 0 |
 * ---------------------------------------
 * | 1-3 | Signature                     |
 * ---------------------------------------
 * | 4   | Protocol Version              |
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
        // header 4 protocol version
        short b4 = in.readUnsignedByte();
        if (b4 != Const.PROTOCOL_VERSION_1_0) {
            in.clear();
            throw new DecoderException("Unsupported protocol version " + b4);
        }
        // header 5 reserved
        in.readUnsignedByte();
        // header 6 reserved
        in.readUnsignedByte();
        // header 7-10 remaining length
        int remainingLength = decodeRemainingLength(in);
        if (remainingLength > Const.MESSAGE_MAX_BYTES) {
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
            JavaType type = ObjectMapper.getTypeFactory().constructParametrizedType(Message.class, Message.class, JsonNode.class);
            Message<JsonNode> m = ObjectMapper.readValue(new ByteBufInputStream(in, remainingLength), type);
            m.setPv(b4);    // save protocol version from header to message
            if (m.getTimestamp() <= 0) m.setTimestamp(System.currentTimeMillis());  // if timestamp not provided
            switch (m.getMsgType()) {
                case MessageType.CONNECT:
                    Message<Connect> c = MessageFactory.newMessage(m, ObjectMapper.treeToValue(m.getPayload(), Connect.class));
                    out.add(c);
                    break;
                case MessageType.CONNACK:
                    Message<ConnAck> ca = MessageFactory.newMessage(m, ObjectMapper.treeToValue(m.getPayload(), ConnAck.class));
                    out.add(ca);
                    break;
                case MessageType.DISCONNECT:
                    Message<Disconnect> d = MessageFactory.newMessage(m, ObjectMapper.treeToValue(m.getPayload(), Disconnect.class));
                    out.add(d);
                    break;
                case MessageType.DISCONNACK:
                    Message<DisconnAck> da = MessageFactory.newMessage(m, ObjectMapper.treeToValue(m.getPayload(), DisconnAck.class));
                    out.add(da);
                    break;
                case MessageType.TRIGGER:
                    Message<Trigger> t = MessageFactory.newMessage(m, ObjectMapper.treeToValue(m.getPayload(), Trigger.class));
                    out.add(t);
                    break;
                case MessageType.TRIGACK:
                    Message<TrigAck> ta = MessageFactory.newMessage(m, ObjectMapper.treeToValue(m.getPayload(), TrigAck.class));
                    out.add(ta);
                    break;
                case MessageType.ACTION:
                    Message<Action> a = MessageFactory.newMessage(m, ObjectMapper.treeToValue(m.getPayload(), Action.class));
                    out.add(a);
                    break;
                case MessageType.ACTACK:
                    Message<ActAck> aa = MessageFactory.newMessage(m, ObjectMapper.treeToValue(m.getPayload(), ActAck.class));
                    out.add(aa);
                    break;
                default:
                    in.clear();
                    throw new DecoderException("Unexpected message type: " + m.getMsgType());
            }
        } catch (IOException e) {
            in.clear();
            throw new DecoderException(e);
        }
    }
}
