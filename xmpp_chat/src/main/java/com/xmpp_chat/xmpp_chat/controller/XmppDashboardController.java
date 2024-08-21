package com.xmpp_chat.xmpp_chat.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.chat2.ChatManager;
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

import com.xmpp_chat.xmpp_chat.Models.ChatMessageDTO;
import com.xmpp_chat.xmpp_chat.services.XmppClient;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class XmppDashboardController {
    @Autowired
    private XmppClient xmppClient;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    private Map<String, List<String>> activeChatsMap = new HashMap<>(); // Stores active chats

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
        System.out.println("it should log out");
        xmppClient.disconnect();
        return "redirect:/login";
    }
    
    @PostMapping("/message")
    @ResponseBody
    public String postMessage(@RequestBody Map<String, String> payload) {
    String message = payload.get("message");
    String destination = payload.get("destination");
    System.out.println("Received message: " + message);
    System.out.println("to destination: " + destination);
    // Process the message here (e.g., broadcast to other users, save to DB, etc.)
    try{xmppClient.sendMessage(destination,message);}
    catch(Exception e){return e.getMessage();}
    return "Message received: " + message;
   }
    
    @PostMapping("/delete")
    @ResponseBody
    public String postMethodName(@RequestBody String entity) {
        //TODO: process POST request
        
        return entity;
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
            xmppClient.addContact(payload.get("contactJid"));
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
    @GetMapping("/chat/{username}")
    @ResponseBody
    public List<String> getChatMessages(@PathVariable("username") String username) {
        System.out.println("Received request for " + username );
        System.out.println("Keys are: "+ activeChatsMap.keySet().toString());
        return activeChatsMap.getOrDefault(username, new ArrayList<>());
    }
}
