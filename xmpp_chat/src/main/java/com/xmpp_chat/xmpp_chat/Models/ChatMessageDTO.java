package com.xmpp_chat.xmpp_chat.Models;

public class ChatMessageDTO {
    private String from;
    private String body;

    public ChatMessageDTO(String from, String body) {
        this.from = from;
        this.body = body;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}

