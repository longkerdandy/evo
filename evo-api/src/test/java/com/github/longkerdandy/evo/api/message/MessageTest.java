package com.github.longkerdandy.evo.api.message;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.longkerdandy.evo.api.protocol.Const;
import com.github.longkerdandy.evo.api.protocol.MessageType;
import org.junit.Test;

import java.io.IOException;

import static com.github.longkerdandy.evo.api.util.JsonUtils.ObjectMapper;

/**
 * Message Test
 */
public class MessageTest {

    @Test
    public void jsonTest() throws IOException {
        // Message, payload is ConnectMessage
        Connect connMsg = new Connect();
        connMsg.setToken("Token 1");
        Message<Connect> out = new Message<>();
        out.setPv(Const.PROTOCOL_VERSION_1_0);
        out.setUserId("User 1");
        out.setMsgId("Message ID 1");
        out.setMsgType(MessageType.CONNECT);
        out.setFrom("Device 1");
        out.setTimestamp(System.currentTimeMillis());
        out.setPayload(connMsg);

        // Serialization
        String json = ObjectMapper.writeValueAsString(out);

        // Deserialization (raw message)
        JavaType type = ObjectMapper.getTypeFactory().constructParametrizedType(Message.class, Message.class, JsonNode.class);
        Message<JsonNode> in = ObjectMapper.readValue(json, type);
        // assert in.getPv() == Const.PROTOCOL_VERSION_1_0;
        assert in.getUserId().equals("User 1");
        assert in.getMsgId().equals("Message ID 1");
        assert in.getMsgType() == MessageType.CONNECT;
        assert in.getFrom().equals("Device 1");
        assert in.getTimestamp() > 0;
        assert in.getPayload() != null;

        // Deserialization (connect message)
        connMsg = ObjectMapper.treeToValue(in.getPayload(), Connect.class);
        assert connMsg.getToken().equals("Token 1");
    }
}
