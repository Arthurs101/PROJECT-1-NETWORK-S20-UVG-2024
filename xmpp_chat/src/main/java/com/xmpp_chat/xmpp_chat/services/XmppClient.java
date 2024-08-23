package com.xmpp_chat.xmpp_chat.services;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.packet.DiscoverItems;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.xmpp_chat.xmpp_chat.Models.RoomDTO;
@Service
public class XmppClient {
    private XmppClient singleton = null;
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
        if (connection.isConnected()){
            this.accountManager = AccountManager.getInstance(connection);
        }
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

    public Map<String, String> DeleteAccount() {
        if(!this.connection.isConnected()){
           return new HashMap<String, String>(){{put("succes","false"); put("message","not even sig in");}};
        }else{
            try {
                accountManager = AccountManager.getInstance(connection);
                connection = null;
                accountManager.deleteAccount();
                return new HashMap<String, String>(){{put("succes","true"); put("message","deleted account");}};
            }catch (Exception e){
                return new HashMap<String, String>(){{put("succes","true"); put("message",e.getMessage());}};
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
        if (this.connection.isConnected()) {
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

    public  List<RoomDTO> getJoinedGroups() {
         MultiUserChatManager multiUserChatManager = MultiUserChatManager.getInstanceFor(connection);
        List<EntityBareJid> joinedRooms = new ArrayList<>(multiUserChatManager.getJoinedRooms());

        List<RoomDTO> roomDtos = new ArrayList<>();
        for (EntityBareJid bareJid : joinedRooms) {
            roomDtos.add(new RoomDTO(bareJid.toString()));
        }

        return roomDtos;
    }

    public void createGroup(String roomName, String nickname) throws Exception {
        MultiUserChatManager mucManager = MultiUserChatManager.getInstanceFor(connection);
        EntityBareJid roomJid = JidCreate.entityBareFrom(roomName + "@conference." + connection.getXMPPServiceDomain());
        MultiUserChat muc = mucManager.getMultiUserChat(roomJid);
        // Create the room and configure it if not already exists
        muc.create(Resourcepart.from(nickname));
        // MucCreateConfigFormHandle handle = muc.;
        joinGroup(roomName, nickname);
    }

    public MultiUserChat joinGroup(String JID, String nickname) throws Exception {
        MultiUserChatManager mucManager = MultiUserChatManager.getInstanceFor(connection);
        EntityBareJid roomJid = JidCreate.entityBareFrom(JID);
        MultiUserChat muc = mucManager.getMultiUserChat(roomJid);
        // Join the group (room)
        muc.join(Resourcepart.from(nickname));
        return muc;
    }

    public List<Map<String, String>> retrieveChatRooms() {
        try {
            List<Map<String, String>> allChatRooms = new ArrayList<>();
            // Create a ServiceDiscoveryManager
            ServiceDiscoveryManager discoManager = ServiceDiscoveryManager.getInstanceFor(connection);

            // Specify the conference domain
            DomainBareJid serviceName = JidCreate.domainBareFrom("conference.alumchat.lol");

            // Discover the items (chat rooms) on the server
            DiscoverItems discoverItems = discoManager.discoverItems(serviceName);

            for (DiscoverItems.Item item : discoverItems.getItems()) {
                Map<String, String> entry = new HashMap<String, String>();
                entry.put("JID" , item.getEntityID().toString());
                entry.put("Name" , item.getName());
                allChatRooms.add(entry);
            }
            return allChatRooms;
        } catch (Exception e) {
            Map<String, String> err = new HashMap<String, String>();
            err.put("error", e.getMessage());
            List<Map<String, String>> allChatRooms = new ArrayList<>();
            allChatRooms.add(err);
            return allChatRooms;
        }
    }   
}

