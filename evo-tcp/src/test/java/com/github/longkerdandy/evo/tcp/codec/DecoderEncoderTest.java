package com.github.longkerdandy.evo.tcp.codec;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.longkerdandy.evo.api.message.ConnectMessage;
import com.github.longkerdandy.evo.api.message.Message;
import com.github.longkerdandy.evo.api.protocol.MessageType;
import com.github.longkerdandy.evo.api.protocol.Protocol;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.github.longkerdandy.evo.api.util.JsonUtils.OBJECT_MAPPER;

/**
 * Decoder & Encoder Test
 */
public class DecoderEncoderTest {

    @Test
    @SuppressWarnings("unchecked")
    public void decoderEncoderTest() throws Exception {
        // Message, payload is ConnectMessage
        ConnectMessage connMsg = new ConnectMessage();
        connMsg.setProtocolVersion(Protocol.VERSION_1_0);
        connMsg.setUser("User 1");
        connMsg.setToken("Token 1");
        Message<ConnectMessage> msgOut = new Message<>();
        msgOut.setMsgId("Message ID 1");
        msgOut.setMsgType(MessageType.CONNECT);
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
        assert msgIn.getFrom().equals("Device 1");
        assert msgIn.getTimestamp() > 0;
        assert msgIn.getPayload() != null;

        connMsg = OBJECT_MAPPER.treeToValue(msgIn.getPayload(), ConnectMessage.class);
        assert connMsg.getProtocolVersion().equals(Protocol.VERSION_1_0);
        assert connMsg.getUser().equals("User 1");
        assert connMsg.getToken().equals("Token 1");
    }
}
