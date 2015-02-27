package com.github.longkerdandy.evo.tcp.util;

import com.fasterxml.uuid.EthernetAddress;
import com.github.longkerdandy.evo.api.util.UuidUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Constant
 */
public class Const {

    private static String NODE_ID;

    /**
     * Get TCP Node Id
     */
    public static String getNodeId() {
        if (StringUtils.isNoneBlank(NODE_ID)) {
            return NODE_ID;
        }

        // get local hardware(mac) address
        EthernetAddress ea = EthernetAddress.fromInterface();
        if (ea == null) throw new IllegalStateException("Can't locate a valid Ethernet Address");

        try {
            URI uri = new URI("https://github.com/longkerdandy/evo/tcp?mac=" + ea.toString());
            NODE_ID = UuidUtils.shortUuid(uri);
        } catch (URISyntaxException ignore) {
            // never happens
        }
        return NODE_ID;
    }

    private Const() {
    }
}
