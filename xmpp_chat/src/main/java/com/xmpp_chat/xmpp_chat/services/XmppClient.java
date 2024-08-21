package com.xmpp_chat.xmpp_chat.services;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.packet.PresenceBuilder;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.jid.parts.Resourcepart;
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
    private String currUsername;
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
        this.currUsername = username;
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
            this.currUsername = username;
            return true;
        } else {
            System.out.println("El servidor no soporta la creación de cuentas");
        }
        return false;
    }

    public String[]  DeleteAccount() {
        if(this.accountManager == null){
            String[] msg= {"false","no account session"};
            return msg;
        }else{
            try {
                this.accountManager.deleteAccount();
                String[] msg= {"true","success on deletion"};
                return msg;
            }catch (Exception e){
                String[] msg= {"false",e.getMessage()};
                return msg;
            }
        }
    }
     
    public Map<String, String> updateStatus(String status, String message) {
        Presence presence;
        Map<String, String> response = new HashMap<>();
        try {
            presence = switch (status) {
                case "available" -> PresenceBuilder.buildPresence()
                        .ofType(Presence.Type.available)
                        .setMode(Mode.available)
                        .setStatus(message)
                        .build();
                case "away" -> PresenceBuilder.buildPresence()
                        .ofType(Presence.Type.available)
                        .setMode(Mode.away)
                        .setStatus(message)
                        .build();
                case "dnd" -> PresenceBuilder.buildPresence()
                        .ofType(Presence.Type.available)
                        .setMode(Mode.dnd)
                        .setStatus(message)
                        .build();
                case "xa" -> PresenceBuilder.buildPresence()
                        .ofType(Presence.Type.available)
                        .setMode(Mode.xa)
                        .setStatus(message)
                        .build();
                default -> PresenceBuilder.buildPresence()
                        .ofType(Presence.Type.available)
                        .setMode(Mode.available)
                        .setStatus(message)
                        .build();
            };

            connection.sendStanza(presence);
            response.put("success", "true");
            response.put("message", "Status changed");
        } catch (Exception e) {
            response.put("success", "false");
            response.put("message", e.getMessage());
            System.err.println(e.getMessage());
        }

        return response;

    }

    public  String[] agregarContacto(String jidStr) {
        if (this.connection != null && this.connection.isAuthenticated()) {
            try {
                EntityBareJid jid = JidCreate.entityBareFrom(jidStr);
                Roster roster = Roster.getInstanceFor(connection);
                roster.createEntry(jid, jidStr, null);
                System.out.println("");
                String[] msg= {"true","Solicitud enviada exitosamente"};
                return msg;
            } catch (Exception e) {
                String[] msg= {"false",e.getMessage()};
                return msg;
            }
        } else {
            String[] msg= {"false","no account isntance"};
            return msg;
        }
    }

    public void sendMessage(String to, String messageBody) throws XmppStringprepException, SmackException.NotConnectedException, InterruptedException {
        if (connection != null && connection.isAuthenticated()) {
            ChatManager chatManager = ChatManager.getInstanceFor(this.connection);
            Chat chat = chatManager.chatWith(JidCreate.entityBareFrom(to));
            chat.send(messageBody);
            
        } else {
            System.out.println("No auth session available");
        }
    }
    
    public ChatManager getChatManagerListener() {
        if (this.connection != null) {
        ChatManager chatManager = ChatManager.getInstanceFor(connection);

        return chatManager;
        } else {
            return null;
        }
    }

    public String getUsername() {
        return this.currUsername;
    }
    
    public void addContact(String contactName) throws SmackException.NotLoggedInException, SmackException.NoResponseException, XMPPException.XMPPErrorException, SmackException.NotConnectedException, InterruptedException, XmppStringprepException {
        Roster roster = Roster.getInstanceFor(connection);
        // Add the contact
        EntityBareJid jid = JidCreate.entityBareFrom(contactName);
        roster.createEntry(jid, contactName,null);
    }
    
    public List<Map<String, String>> getContacts() throws Exception {
    Roster roster = Roster.getInstanceFor(connection);
        if (!roster.isLoaded()) {
            roster.reloadAndWait();
        }

        List<Map<String, String>> contacts = new ArrayList<>();
        for (RosterEntry entry : roster.getEntries()) {
            Map<String, String> contactInfo = new HashMap<>();
            contactInfo.put("jid", entry.getJid().toString());
            contactInfo.put("name", entry.getName() != null ? entry.getName() : entry.getJid().toString());
            contactInfo.put("status", roster.getPresence(entry.getJid()).getStatus());
            contactInfo.put("groups", getGroups(entry));
            contacts.add(contactInfo);
        }
    
    return contacts;
    }
    private String getGroups(RosterEntry entry) {
        List<RosterGroup> groups = entry.getGroups();
        if (groups.isEmpty()) {
            return "";
        }
        StringBuilder groupsString = new StringBuilder();
        for (RosterGroup group : groups) {
            if (groupsString.length() > 0) {
                groupsString.append(", ");
            }
            groupsString.append(group.getName());
        }
        return groupsString.toString();
    }

    public void createGroup(String roomName, String nickname) throws Exception {
        MultiUserChatManager mucManager = MultiUserChatManager.getInstanceFor(connection);
        EntityBareJid roomJid = JidCreate.entityBareFrom(roomName + "@conference." + connection.getXMPPServiceDomain());
        MultiUserChat muc = mucManager.getMultiUserChat(roomJid);
        // Create the room and configure it if not already exists
        muc.create(Resourcepart.from(nickname));
        // MucCreateConfigFormHandle handle = muc.;
    }

    public void joinGroup(String roomName, String nickname) throws Exception {
         MultiUserChatManager mucManager = MultiUserChatManager.getInstanceFor(connection);
        EntityBareJid roomJid = JidCreate.entityBareFrom(roomName + "@conference." + connection.getXMPPServiceDomain());
        MultiUserChat muc = mucManager.getMultiUserChat(roomJid);
        
        // Join the group (room)
        muc.join(Resourcepart.from(nickname));
    }

    // public List<String> getGroups() throws Exception {
    //     MultiUserChatManager mucManager = MultiUserChatManager.getInstanceFor(connection);
    //     List<String> rooms = new ArrayList<>();
        
    //     // Discover all rooms
    //     DiscoverItems items = mucManager.getHostedRooms(connection.getXMPPServiceDomain());
    //     for (DiscoverItems.Item item : items.getItems()) {
    //         rooms.add(item.getEntityID().toString());
    //     }
        
    //     return rooms;
    // }

    public List<String> getOnlineUsers() {
        Roster roster = Roster.getInstanceFor(connection);
    List<String> onlineUsers = new ArrayList<>();

    for (RosterEntry entry : roster.getEntries()) {
        Presence presence = roster.getPresence(entry.getJid());
        if (presence.isAvailable()) {
            onlineUsers.add(entry.getJid().toString());
        }
    }
    
        return onlineUsers;
    }

    
}

