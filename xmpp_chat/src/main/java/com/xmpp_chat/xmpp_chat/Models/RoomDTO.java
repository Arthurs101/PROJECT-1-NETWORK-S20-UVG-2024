package com.xmpp_chat.xmpp_chat.Models;

public class RoomDTO {
    private String JID;

    public RoomDTO(String roomJid) {
        this.JID = roomJid;
    }

    public String getRoomJid() {
        return JID;
    }

    public void setRoomJid(String roomJid) {
        this.JID = roomJid;
    }
} 
