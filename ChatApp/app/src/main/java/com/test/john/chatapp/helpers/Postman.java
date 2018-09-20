package com.test.john.chatapp.helpers;

import com.test.john.chatapp.client.Connection;
import com.test.john.chatapp.Message;

public class Postman extends Thread {

    private Message message;
    private Connection connection;

    public Postman(Message message, Connection connection) {
        this.message = message;
        this.connection = connection;
    }

    @Override
    public void run() {
        connection.sendMessage(message);
    }
}
