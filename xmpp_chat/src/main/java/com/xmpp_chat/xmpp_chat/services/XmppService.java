package com.xmpp_chat.xmpp_chat.services;

import java.io.IOException;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class XmppService {
    @Value("${xmpp.domain}")
    private String xmppDomain;

    private XMPPTCPConnection connection;

    public void connect(String username, String password) throws IOException, InterruptedException, SmackException, XMPPException {
        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                .setUsernameAndPassword(username, password)
                .setXmppDomain(xmppDomain)
                // .setHost("hostname")
                .setPort(5222)
                .setSecurityMode(ConnectionConfiguration.SecurityMode.ifpossible)
                .build();

        connection = new XMPPTCPConnection(config);
        connection.connect();  // This method is specific to XMPPTCPConnection
        connection.login();
    }

    public void disconnect() {
        if (connection != null && connection.isConnected()) {
            connection.disconnect();
        }
    }

    // Add other methods to send/receive messages
}
