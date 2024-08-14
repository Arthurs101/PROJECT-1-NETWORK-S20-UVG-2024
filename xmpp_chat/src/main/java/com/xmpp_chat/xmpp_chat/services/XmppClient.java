package com.xmpp_chat.xmpp_chat.services;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.stringprep.XmppStringprepException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class XmppClient {
    private XmppClient singleton = null;
    public XmppClient getSingleton() { 
        if (singleton == null) {
            singleton = new XmppClient();
    }return singleton; }
    private XMPPTCPConnection connection;
    private AccountManager accountManager;
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
        accountManager = AccountManager.getInstance(connection);
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

    public boolean DeleteAccount() {
        if(this.accountManager == null){
            return false;
        }else{
            try {
                this.accountManager.deleteAccount();
            }catch (Exception e){
                return false;
            }
        }
        return false;

    }
     
    public  void agregarContacto(String jidStr) {
        if (this.connection != null && this.connection.isAuthenticated()) {
            try {
                EntityBareJid jid = JidCreate.entityBareFrom(jidStr);
                Roster roster = Roster.getInstanceFor(connection);
                roster.createEntry(jid, jidStr, null);
                System.out.println("Contacto agregado exitosamente.");
            } catch (Exception e) {
                System.out.println("Error al agregar contacto: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("No has iniciado sesión.");
        }
    }

    public void sendMessage(String to, String messageBody) throws XmppStringprepException, SmackException.NotConnectedException, InterruptedException {
        if (connection != null && connection.isAuthenticated()) {
            ChatManager chatManager = ChatManager.getInstanceFor(this.connection);
            System.out.println(to + "@" + this.domain);
            Chat chat = chatManager.chatWith(JidCreate.entityBareFrom(to + "@" + this.domain));
            chat.send(messageBody);
            
        } else {
            System.out.println("No auth session available");
        }
    }

    public ChatManager getChatManagerListener() {
        ChatManager chatManager = ChatManager.getInstanceFor(connection);

        return chatManager;
    }
    
    
}

