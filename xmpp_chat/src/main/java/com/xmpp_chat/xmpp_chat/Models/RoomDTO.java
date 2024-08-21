package com.xmpp_chat.xmpp_chat.Models;

public class RoomDTO {
    private String roomJid;

    public RoomDTO(String roomJid) {
        this.roomJid = roomJid;
    }

    public String getRoomJid() {
        return roomJid;
    }

    public void setRoomJid(String roomJid) {
        this.roomJid = roomJid;
    }
} 
