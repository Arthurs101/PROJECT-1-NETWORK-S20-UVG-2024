package com.xmpp_chat.xmpp_chat.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jxmpp.jid.EntityBareJid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.xmpp_chat.xmpp_chat.Models.ChatMessageDTO;
import com.xmpp_chat.xmpp_chat.Models.RoomDTO;
import com.xmpp_chat.xmpp_chat.services.XmppClient;

import org.springframework.web.bind.annotation.RequestParam;

import com.xmpp_chat.xmpp_chat.Models.RoomMessageDTO;


@Controller
public class XmppDashboardController {
    @Autowired
    private XmppClient xmppClient;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    private Map<String, List<String>> activeChatsMap = new HashMap<>(); // Stores active chats
    private Map<String, MultiUserChat> activeRoomsListeners = new HashMap<>(); // Stores all the multiuser chats objects thar can send messages also
    @GetMapping("/dashboard")
    public String getDashboard(Model model) {
        xmppClient.updateStatus("available", "");
        //render the dashboard and set a listener for any incoming messages
        ChatManager chatManager = xmppClient.getChatManagerListener();
        if (chatManager != null) {
        chatManager.addIncomingListener((from, message, chat) -> {
            this.activeChatsMap.computeIfAbsent(from.toString(), k -> new ArrayList<>()).add(message.getBody());
            // Convert the XMPP message to a DTO and send it via WebSocket
            ChatMessageDTO chatMessageDTO = new ChatMessageDTO(from.toString(), message.getBody());
            messagingTemplate.convertAndSendToUser(xmppClient.getUsername(), "/queue/messages", chatMessageDTO);
        });
        model.addAttribute("activeChats", new ArrayList<String>(this.activeChatsMap.keySet()));
        model.addAttribute("nickname", xmppClient.getUsername());       
        return "dashboard";}
        else{
            return "redirect:/login";
        }
    }
    @PostMapping("/logout")
    public String logout() {
        xmppClient.disconnect();
        return "redirect:/login";
    }
    
    @PostMapping("/message")
    @ResponseBody
    public  HashMap<String, String> postMessage(@RequestBody Map<String, String> payload) {
    String message = payload.get("message");
    String destination = payload.get("destination");
    // Process the message here (e.g., broadcast to other users, save to DB, etc.)
    try{
        if(activeRoomsListeners.containsKey(destination)){
            activeRoomsListeners.get(destination).sendMessage(message);
        }
        else {xmppClient.sendMessage(destination,message); }
        
    }
    catch(Exception e){
        return new HashMap<String, String>() {{
            put("succes","false");
            put("message",e.getMessage());
        }};
    }
    return new HashMap<String, String>() {{put("succes","true");}};
   }
   
   @PostMapping("/message/file")
   @ResponseBody
    public  HashMap<String, String> postMessageFile(@RequestParam("file") MultipartFile file, 
                                                 @RequestParam("recipientJid") String recipientJid,
                                                 @RequestParam("description") String description) {
        try{
            xmppClient.fileSender(recipientJid,file,description);
            return new HashMap<String, String>(){{put("succes","true");}};
        }catch(Exception e){
            return new HashMap<String, String>(){{put("succes","false"); put("message",e.getMessage());}};
        }
    }
   

    @PostMapping("/delete")
    @ResponseBody
    public Map<String, String> postMethodName() {
        Map<String, String> response = xmppClient.DeleteAccount();
        // Check if the operation was successful
        if ("true".equals(response.get("succes"))) {
            response.put("redirect", "/login");
        }
        return response;
    }
    
    @PostMapping("/set-status")
    @ResponseBody
    public Map<String, String> setStatus(@RequestBody Map<String, String> payload) {
        String status = payload.get("status");
        String message = payload.get("message");
        try {
            return xmppClient.updateStatus(status, message);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("success", "false");
            response.put("message", e.getMessage());
            System.err.println(e.getMessage());
            return response;
        }
    }

    @PostMapping("/add-contact")
    @ResponseBody
    public String addContact(@RequestBody Map<String, String> payload) {
        try {
            xmppClient.removeContact(payload.get("contactJid"));
            return "succes";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to remove contact: " + e.getMessage();
        }
    }

    @PostMapping("/remove-contact")
    @ResponseBody
    public String removeContact(@RequestBody Map<String, String> payload) {
        try {
            xmppClient.removeContact(payload.get("contactJid"));
            return "succes";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to add contact: " + e.getMessage();
        }
    }

    @GetMapping("/get-contacts")
    @ResponseBody
    public List<Map<String, String>> getContacts() {
        try {return xmppClient.getContacts();}
        catch(Exception e){return new ArrayList<Map<String, String>>();}
        
    }

    @GetMapping("/rooms")
    @ResponseBody
    public List<Map<String, String>> getRooms() {
        return xmppClient.retrieveChatRooms();
    }

    @PostMapping("/rooms")
    @ResponseBody
    public Map<String, String> newRoom(@RequestBody HashMap<String, String> roomMap) { //creating a new room
        try{
            if(roomMap.get("roomName").equals("")){
                return new HashMap<String, String>(){{put("succes","false");put("message","name cannot be empty");}};
            }
            xmppClient.createGroup(roomMap.get("roomName"),roomMap.get("nickname"));
            return new HashMap<String, String>() {{put("succes","true");}};
        }catch(Exception e){
            return new HashMap<String, String>() {{put("succes","false");put("message",e.getMessage());}};
        }
    }
    @GetMapping("/rooms/mine")
    @ResponseBody
    public List<RoomDTO> getMethodName() {
        return xmppClient.getJoinedGroups();
    }
    
    @PostMapping("/rooms/join")
    @ResponseBody
    public Map<String, String> joinRoom(@RequestBody HashMap<String,String> room){
        try{
            if (!activeRoomsListeners.containsKey(room.get("JID"))){ //avoid duplication on listeners for a room
                MultiUserChat muc = xmppClient.joinGroup(room.get("JID"),room.get("Name"));
                muc.addMessageListener(new MessageListener() {
                    @Override
                    public void processMessage(Message message) {
                        // Handle the received message
                        if (message.getBody() != null) {
                            // Convert the XMPP message to a DTO and send it via WebSocket
                            ChatMessageDTO chatMessageDTO = new ChatMessageDTO(message.getFrom().getResourceOrEmpty().toString(), message.getBody());
                            RoomMessageDTO roomMessageDTO = new RoomMessageDTO(room.get("JID"),chatMessageDTO);
                            messagingTemplate.convertAndSendToUser(xmppClient.getUsername(), "/queue/group-messages", roomMessageDTO);
                        }
                    }
                    
                }

                );
                activeRoomsListeners.put(room.get("JID"), muc);
            }
           

            return new HashMap<String, String>() {{put("succes","true");
        
        }};
        } catch(Exception e){
            return new HashMap<String, String>() {{put("succes","false");put("message",e.getMessage());}};
        }
    }
    @GetMapping("/chat/{username}")
    @ResponseBody
    public List<String> getChatMessages(@PathVariable("username") String username) {
        return activeChatsMap.getOrDefault(username, new ArrayList<>());
    }
}
