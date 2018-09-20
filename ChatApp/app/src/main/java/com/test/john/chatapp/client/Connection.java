package com.test.john.chatapp.client;

import com.test.john.chatapp.Message;
import com.test.john.chatapp.helpers.JsonConverter;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection implements Closeable {

    private final Socket socket;
    private final ObjectOutputStream outputStream;
    private final ObjectInputStream inputStream;

    Connection(Socket socket) throws IOException {
        this.socket = socket;
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.inputStream = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
        outputStream.close();
        socket.close();
    }

    //получаем сообщение в формате JSON
    Message receiveMessage() throws IOException {
        Message message;
        synchronized (inputStream) {
            String incomingMessage = inputStream.readUTF();
            message = JsonConverter.convertFromJson(incomingMessage, Message.class);
            return message;
        }
    }

    //отправляем сообщение в формате JSON
    public void sendMessage(Message message) {
        synchronized (outputStream) {
            try {
                outputStream.writeUTF(JsonConverter.convertToJson(message));
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}