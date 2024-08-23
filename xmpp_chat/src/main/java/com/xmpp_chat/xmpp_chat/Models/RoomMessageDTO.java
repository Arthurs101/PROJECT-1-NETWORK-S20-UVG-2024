package com.xmpp_chat.xmpp_chat.Models;

public class RoomMessageDTO {
    private String JID;
    private ChatMessageDTO message;
    public RoomMessageDTO(String roomJid, ChatMessageDTO message) {
        this.JID = roomJid;
        this.message = message;
    }

    public String getRoomJid() {
        return JID;
    }

    public void setRoomJid(String roomJid) {
        this.JID = roomJid;
    }
    public String getFrom() {
        return message.getFrom();
    }

    public void setFrom(String from) {
        this.message.setFrom(from);
    }

    public String getBody() {
        return this.message.getBody();
    }

    public void setBody(String body) {
        this.message.setBody(body);
    }
}
