package com.github.longkerdandy.evo.tcp.codec;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.longkerdandy.evo.api.message.Connect;
import com.github.longkerdandy.evo.api.message.Message;
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
        Connect connMsg = new Connect();
        connMsg.setUser("User 1");
        connMsg.setToken("Token 1");
        Message<Connect> msgOut = new Message<>();
        msgOut.setMsgId("Message ID 1");
        msgOut.setMsgType(MessageType.CONNECT);
        msgOut.setProtocolVersion(Const.PROTOCOL_VERSION_1_0);
        msgOut.setFrom("Device 1");
        msgOut.setTimestamp(System.currentTimeMillis());
        msgOut.setPayload(connMsg);

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

        assert msgIn.getMsgId().equals("Message ID 1");
        assert msgIn.getMsgType().equals(MessageType.CONNECT);
        assert msgIn.getProtocolVersion().equals(Const.PROTOCOL_VERSION_1_0);
        assert msgIn.getFrom().equals("Device 1");
        assert msgIn.getTimestamp() > 0;
        assert msgIn.getPayload() != null;

        connMsg = ObjectMapper.treeToValue(msgIn.getPayload(), Connect.class);
        assert connMsg.getUser().equals("User 1");
        assert connMsg.getToken().equals("Token 1");
    }
}
