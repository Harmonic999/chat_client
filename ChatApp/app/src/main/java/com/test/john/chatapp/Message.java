package com.test.john.chatapp;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.io.Serializable;

@JsonAutoDetect
public class Message implements Serializable {

    private String data;
    private MessageType type;

    public Message(String data, MessageType type) {
        this.data = data;
        this.type = type;
    }

    public Message(MessageType type) {
        this.type = type;
        this.data = null;
    }

    //пустой конструктор для Jackson
    public Message() {
    }

    public String getData() {
        return data;
    }

    public MessageType getType() {
        return type;
    }
}
