package com.xmpp_chat.xmpp_chat.services;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jxmpp.jid.parts.Localpart;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;



@Service
public class XmppClient {
    private XMPPTCPConnection connection;

    @Value("${xmpp.domain}")
    private String domain;

    @Value("${xmpp.host}")
    private String host;

    public boolean connect(String username, String password) throws IOException, InterruptedException, SmackException, XMPPException {
        
        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
            .setUsernameAndPassword(username, password)
            .setXmppDomain(domain)
            .setHost(host)
            .setPort(5222)
            .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
            .build();

        connection = new XMPPTCPConnection(config);
        connection.connect();
        connection.login();
        return connection.isConnected();
    }

    public void disconnect() {
        if (connection != null && connection.isConnected()) {
            connection.disconnect();
        }
    }

    public boolean register(String username, String password , String email , String fullName) throws IOException, InterruptedException, SmackException, XMPPException {
        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                    .setXmppDomain(domain)
                    .setHost(domain)
                    .setPort(5222)
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    .build();

        connection = new XMPPTCPConnection(config);
        connection.connect();
        AccountManager accountManager = AccountManager.getInstance(connection);
        accountManager.sensitiveOperationOverInsecureConnection(true);
        if (accountManager.supportsAccountCreation()) {
            Localpart localUsr = Localpart.from(username);
            Map<String, String> attributes = new HashMap<>();
            attributes.put("email", email);
            attributes.put("name", fullName);
            accountManager.createAccount(localUsr, password,attributes);
            return true;
        } else {
            System.out.println("El servidor no soporta la creación de cuentas");
        }
        return false;
    }
    // Add other methods to send/receive messages
}

