package com.xmpp_chat.xmpp_chat.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xmpp_chat.xmpp_chat.services.XmppClient;


@Controller
public class XmppDashboardController {
    @Autowired
    private XmppClient xmppClient;


    @GetMapping("/dashboard")
    public String getDashboard() {
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
