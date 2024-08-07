package com.xmpp_chat.xmpp_chat.controller;

import java.io.IOException;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.xmpp_chat.xmpp_chat.services.XmppClient;

@Controller
public class XmppAuthController {

    @Autowired
    private XmppClient xmppClient;


    @GetMapping("/login")
    public String loginPage(){
        return "login";
    }
    @PostMapping("/login")
    public String loginRquest(@RequestParam String username, @RequestParam String pwrd, Model model) {
        boolean isLoggedIn = false;
        try {
            isLoggedIn = xmppClient.connect(username, pwrd);
        } catch (IOException e) {

            model.addAttribute("alertMessage", e.getMessage());
        } catch (InterruptedException e) {

            model.addAttribute("alertMessage", e.getMessage());
        }
         catch (SmackException e) {

            model.addAttribute("alertMessage", e.getMessage());
        
        } catch (XMPPException e) {

            model.addAttribute("alertMessage", e.getMessage());
        }

        if (isLoggedIn) {
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "login"; // Return to the login page with an error
        }
    }
    
    @GetMapping("/register")
    public String registerPage(){
        return "register";
    }
    @PostMapping("/register")
    public String registerRequest(
        @RequestParam String username, 
        @RequestParam String pwrd, 
        @RequestParam String email,
        @RequestParam String fullName,
        Model model) {
        boolean isLoggedIn = false;
        try {
            isLoggedIn = xmppClient.register(username, pwrd, pwrd, username);
        } catch (IOException e) {

            model.addAttribute("alertMessage", e.getMessage());
        } catch (InterruptedException e) {

            model.addAttribute("alertMessage", e.getMessage());
        }
         catch (SmackException e) {

            model.addAttribute("alertMessage", e.getMessage());
        
        } catch (XMPPException e) {

            model.addAttribute("alertMessage", e.getMessage());
        }

        if (isLoggedIn) {
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "login"; // Return to the login page with an error
        }
    }
}
