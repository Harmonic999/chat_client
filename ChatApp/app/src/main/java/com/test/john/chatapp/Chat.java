package com.test.john.chatapp;

public interface Chat {

    String getEnteredText();
    void handleNewMessage(String message);
    String getRegisteredChatName();
    void setRegisteredChatName(String name);
}
