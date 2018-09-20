package com.test.john.chatapp.client;

import com.test.john.chatapp.Chat;
import com.test.john.chatapp.Message;
import com.test.john.chatapp.MessageType;
import com.test.john.chatapp.helpers.Postman;

import java.io.IOException;
import java.net.Socket;

public class ConnectionHandler extends Thread implements Client {

    private static final int PORT = 8080;
    private static final String SERVER = "192.168.1.5";

    private ClientState state;
    private Connection connection;

    //Активируем слушателся при получении нового сообщения для изменения TextView в основной нити (UI)
    private Chat chat;

    public ConnectionHandler(Chat chat) {
        this.chat = chat;
        setState(ClientState.IS_UNREGISTERED);
    }

    @Override
    public void run() {
        tryEstablishingConnectionWithServer();
    }

    private void tryEstablishingConnectionWithServer() {
        try {
            Socket socket = new Socket(SERVER, PORT);
            connection = new Connection(socket);

            boolean wasRegisteredBefore = chat.getRegisteredChatName() != null;

            if (!wasRegisteredBefore) { //Если до этого клиент не был зарегистрирован
                chat.handleNewMessage("Введите имя.");
            } else {
                registerAgain(chat.getRegisteredChatName(), connection); //Повторно регистрируем то же имя, что и до перезапуска приложения
            }

            Message message = connection.receiveMessage(); //Вводим имя и ждём ответ сервера
            if (message.getType() == MessageType.NAME_REQUEST) {
                message = connection.receiveMessage(); //Ждём одобрение сервера
                if (message.getType() == MessageType.NAME_ACCEPTED) {

                    chat.setRegisteredChatName(message.getData()); //Сохраняем имя, под которым зарегистрировались
                    setState(ClientState.IS_REGISTERED);
                    chatLoop(connection); //Если сервер принимает имя, входим в основной цикл обработки сообщений, пришедших с сервера
                } else if (message.getType() == MessageType.NAME_REJECTED) {
                    chat.handleNewMessage("Имя не было принято сервером. Попробуйте ещё раз.");
                    tryEstablishingConnectionWithServer(); //Если сервер не принимает имя, пытаемся зарегистрироваться ещё раз
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Основной цикл обработки сообщений
    @SuppressWarnings("all")
    private void chatLoop(Connection connection) throws IOException {
        while (true) {
            Message message = connection.receiveMessage();
            switch (message.getType()) {
                case TEXT:
                    chat.handleNewMessage(message.getData());
                    break;
            }
        }
    }

    private void registerAgain(String userName, Connection connection) {
        new Postman(new Message(userName, MessageType.USER_NAME), connection).start();
    }

    @Override
    public void sendMessage() {
        Message message = null;
        switch (state) {
            case IS_REGISTERED: //Пользователь становится зарегистрированным как только сервер принимает имя и получает право отправлять текстовые сообщения
                message = new Message(chat.getEnteredText(), MessageType.TEXT);
                break;
            case IS_UNREGISTERED: //Отправляем сообщение с типом USER_NAME до тех пор, пока сервер не зарегистрирует пользователя
                message = new Message(chat.getEnteredText(), MessageType.USER_NAME);
                break;
            case IS_LEAVING:
                message = (new Message(MessageType.USER_DISCONNECTED)); //Сообщаем серверу о разъединении перед пересозданием приложения
                break;
        }
        new Postman(message, connection).start(); //Запускаем новую нить для отправки сообщения серверу
    }

    @Override
    public void setState(ClientState state) {
        this.state = state;
    }
}
