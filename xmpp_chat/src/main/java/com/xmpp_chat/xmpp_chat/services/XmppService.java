package com.xmpp_chat.xmpp_chat.services;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.XMPPException;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class XmppService {

    private XMPPConnection connection;

    public void connect(String username, String password) throws IOException, InterruptedException, SmackException, XMPPException {
        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                .setUsernameAndPassword(username, password)
                .setXmppDomain("yourdomain.com")
                .setHost("hostname")
                .setPort(5222)
                .setSecurityMode(ConnectionConfiguration.SecurityMode.ifpossible)
                .build();

        connection = new XMPPTCPConnection(config);
        connection.connect();
        connection.login();
    }

    public void disconnect() {
        if (connection != null && connection.isConnected()) {
            connection.disconnect();
        }
    }

    // Add other methods to send/receive messages
}