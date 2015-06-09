package com.github.longkerdandy.evo.api.netty;

import com.github.longkerdandy.evo.api.message.Connect;
import com.github.longkerdandy.evo.api.message.Message;
import com.github.longkerdandy.evo.api.message.MessageFactory;
import com.github.longkerdandy.evo.api.protocol.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Decoder & Encoder Test
 */
public class DecoderEncoderTest {

    @Test
    @SuppressWarnings("unchecked")
    public void decoderEncoderTest() throws Exception {
        // Message, payload is ConnectMessage
        Message<Connect> msgOut = MessageFactory.newConnectMessage(ProtocolType.TCP_1_0, DeviceType.CONTROLLER_ANDROID_PHONE, "Device 1", null, "Desc 1", "User 1", "Token 1", OverridePolicy.IGNORE, null);

        // encoding
        Encoder encoder = new Encoder();
        ByteBuf buf = Unpooled.buffer();
        encoder.encode(null, msgOut, buf);

        // decoding
        Decoder decoder = new Decoder();
        List<Object> out = new ArrayList<>();
        decoder.decode(null, buf, out);
        Message msgIn = (Message) out.get(0);
        buf.release();

        assert msgIn.getMsgId().equals(msgOut.getMsgId());
        assert msgIn.getMsgType() == MessageType.CONNECT;
        assert msgIn.getProtocol() == ProtocolType.TCP_1_0;
        assert msgIn.getDeviceType() == DeviceType.CONTROLLER_ANDROID_PHONE;
        assert msgIn.getFrom().equals("Device 1");
        assert msgIn.getDescId().equals("Desc 1");
        assert msgIn.getUserId().equals("User 1");
        assert msgIn.getTimestamp() > 0;
        assert msgIn.getPayload() != null;

        Connect connect = (Connect) msgIn.getPayload();
        assert connect.getToken().equals("Token 1");
    }
}
