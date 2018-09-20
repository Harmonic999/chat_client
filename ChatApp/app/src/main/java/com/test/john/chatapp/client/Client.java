package com.test.john.chatapp.client;

public interface Client {
    void sendMessage();

    void setState(ClientState state);
}
