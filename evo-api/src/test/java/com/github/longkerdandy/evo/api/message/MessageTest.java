package com.github.longkerdandy.evo.api.message;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.longkerdandy.evo.api.protocol.DeviceType;
import com.github.longkerdandy.evo.api.protocol.MessageType;
import com.github.longkerdandy.evo.api.protocol.OverridePolicy;
import com.github.longkerdandy.evo.api.protocol.ProtocolType;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.Objects;

import static com.github.longkerdandy.evo.api.util.JsonUtils.ObjectMapper;

/**
 * Message Test
 */
public class MessageTest {

    @Test
    public void jsonTest() throws IOException {
        // message, payload is ConnectMessage
        Message<Connect> out = MessageFactory.newConnectMessage(
                ProtocolType.TCP_1_0, DeviceType.DEVICE,
                "Device A", "Device B", "Description A",
                "User A", "Token A",
                OverridePolicy.IGNORE, null);

        // validate
        out.validate();

        // serialization
        String json = ObjectMapper.writeValueAsString(out);

        // deserialization (raw message)
        JavaType type = ObjectMapper.getTypeFactory().constructParametrizedType(Message.class, Message.class, JsonNode.class);
        Message<Object> in = ObjectMapper.readValue(json, type);

        // validate
        in.validate();

        assert in.getProtocol() == ProtocolType.TCP_1_0;
        assert in.getUserId().equals("User A");
        assert StringUtils.isNotBlank(in.getMsgId());
        assert in.getMsgType() == MessageType.CONNECT;
        assert in.getFrom().equals("Device A");
        assert in.getTimestamp() > 0;
        assert in.getPayload() != null;

        // deserialization (connect message)
        Connect connect = ObjectMapper.treeToValue((JsonNode) in.getPayload(), Connect.class);
        assert connect.getToken().equals("Token A");

        // validate
        in.setPayload(connect);
        in.validate();
    }
}
