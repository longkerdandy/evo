package com.github.longkerdandy.evo.api.message;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.longkerdandy.evo.api.protocol.MessageType;
import com.github.longkerdandy.evo.api.protocol.Protocol;
import org.junit.Test;

import java.io.IOException;

import static com.github.longkerdandy.evo.api.util.JsonUtils.OBJECT_MAPPER;

/**
 * Message JSON serialization and deserialization test
 */
public class JsonTest {

    @Test
    public void jsonTest() throws IOException {
        // Message, payload is ConnectMessage
        ConnectMessage connMsg = new ConnectMessage();
        connMsg.setProtocolVersion(Protocol.VERSION_1_0);
        connMsg.setUser("User 1");
        connMsg.setToken("Token 1");
        Message<ConnectMessage> out = new Message<>();
        out.setMsgId("Message ID 1");
        out.setMsgType(MessageType.CONNECT);
        out.setDevice("Device 1");
        out.setTimestamp(System.currentTimeMillis());
        out.setPayload(connMsg);

        // Serialization
        String json = OBJECT_MAPPER.writeValueAsString(out);

        // Deserialization (raw message)
        JavaType type = OBJECT_MAPPER.getTypeFactory().constructParametricType(Message.class, JsonNode.class);
        Message<JsonNode> in = OBJECT_MAPPER.readValue(json, type);
        assert in.getMsgId().equals("Message ID 1");
        assert in.getMsgType().equals(MessageType.CONNECT);
        assert in.getDevice().equals("Device 1");
        assert in.getTimestamp() > 0;
        assert in.getPayload() != null;

        // Deserialization (connect message)
        connMsg = OBJECT_MAPPER.treeToValue(in.getPayload(), ConnectMessage.class);
        assert connMsg.getProtocolVersion().equals(Protocol.VERSION_1_0);
        assert connMsg.getUser().equals("User 1");
        assert connMsg.getToken().equals("Token 1");
    }
}
