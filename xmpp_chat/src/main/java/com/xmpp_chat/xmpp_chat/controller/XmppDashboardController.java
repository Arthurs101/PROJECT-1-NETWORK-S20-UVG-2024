package com.xmpp_chat.xmpp_chat.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.chat2.ChatManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xmpp_chat.xmpp_chat.services.XmppClient;


@Controller
public class XmppDashboardController {
    @Autowired
    private XmppClient xmppClient;

    private Map<String, List<String>> activeChatsMap = new HashMap<>(); // Stores active chats

    @GetMapping("/dashboard")
    public String getDashboard(Model model) {
        ChatManager chatManager = xmppClient.getChatManagerListener();
        chatManager.addIncomingListener((from, message, chat) -> {
            this.activeChatsMap.computeIfAbsent(from.toString(), k -> new ArrayList<>()).add(message.getBody());
            System.out.println("Recevied message " + from + ":" + message.getBody());
        });
        model.addAttribute("activeChats", new ArrayList<String>(this.activeChatsMap.keySet()));
        return "dashboard";
    }
    


    @PostMapping("/message")
    @ResponseBody
    public String postMessage(@RequestBody Map<String, String> payload) {
    String message = payload.get("message");
    String destination = payload.get("destination");
    System.out.println("Received message: " + message);
    // Process the message here (e.g., broadcast to other users, save to DB, etc.)
    try{xmppClient.sendMessage(destination,message);}
    catch(Exception e){return e.getMessage();}
    return "Message received: " + message;
   }
    
}
