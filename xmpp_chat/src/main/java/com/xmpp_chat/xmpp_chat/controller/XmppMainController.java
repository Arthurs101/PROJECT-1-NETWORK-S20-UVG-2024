package com.xmpp_chat.xmpp_chat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class XmppMainController {
    @GetMapping({"/",""})
    public String getMethodName() {
        return "redirect:login";
    }
    
}
