package com.github.longkerdandy.evo.tcp.codec;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.longkerdandy.evo.api.message.Connect;
import com.github.longkerdandy.evo.api.message.Message;
import com.github.longkerdandy.evo.api.message.MessageFactory;
import com.github.longkerdandy.evo.api.protocol.DeviceType;
import com.github.longkerdandy.evo.api.protocol.MessageType;
import com.github.longkerdandy.evo.api.protocol.Const;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.github.longkerdandy.evo.api.util.JsonUtils.ObjectMapper;

/**
 * Decoder & Encoder Test
 */
public class DecoderEncoderTest {

    @Test
    @SuppressWarnings("unchecked")
    public void decoderEncoderTest() throws Exception {
        // Message, payload is ConnectMessage
        Message<Connect> msgOut = MessageFactory.newConnectMessage(
                DeviceType.CONTROLLER, "Device 1", null, "Desc 1", "User 1", "Token 1", null);

        // encoding
        Encoder encoder = new Encoder();
        ByteBuf buf = Unpooled.buffer();
        encoder.encode(null, msgOut, buf);

        // decoding
        Decoder decoder = new Decoder();
        List<Object> out = new ArrayList<>();
        decoder.decode(null, buf, out);
        Message<JsonNode> msgIn = (Message<JsonNode>) out.get(0);
        buf.release();

        assert msgIn.getMsgId().equals(msgOut.getMsgId());
        assert msgIn.getMsgType() == MessageType.CONNECT;
        assert msgIn.getPv() == Const.PROTOCOL_VERSION_1_0;
        assert msgIn.getPt() == Const.PROTOCOL_TYPE_JSON;
        assert msgIn.getDeviceType() == DeviceType.CONTROLLER;
        assert msgIn.getFrom().equals("Device 1");
        assert msgIn.getDescId().equals("Desc 1");
        assert msgIn.getUserId().equals("User 1");
        assert msgIn.getTimestamp() > 0;
        assert msgIn.getPayload() != null;

        Connect connect = ObjectMapper.treeToValue(msgIn.getPayload(), Connect.class);
        assert connect.getToken().equals("Token 1");
    }
}
