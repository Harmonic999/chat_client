package com.test.john.chatapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.test.john.chatapp.client.Client;
import com.test.john.chatapp.client.ClientState;
import com.test.john.chatapp.client.ConnectionHandler;

public class MainActivity extends Activity implements View.OnClickListener, Chat {

    private String registeredChatName;

    private TextView chatWindow;
    private EditText messageInput;
    private ScrollView scroll;

    private Client client;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putCharSequence("chatHistory", chatWindow.getText());
        outState.putString("registeredChatName", registeredChatName);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();

        if (savedInstanceState != null) {
            registeredChatName = savedInstanceState.getString("registeredChatName");
            chatWindow.setText(savedInstanceState.getCharSequence("chatHistory"));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        ConnectionHandler connectionHandler = new ConnectionHandler(this);
        client = connectionHandler;
        connectionHandler.start();
    }

    private void initializeViews() {
        chatWindow = findViewById(R.id.chatWindow);
        scroll = findViewById(R.id.scroller);
        messageInput = findViewById(R.id.messageInput);
        Button sendBtn = findViewById(R.id.sendBtn);
        sendBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sendBtn:
                if (!messageInput.getText().toString().isEmpty()) { //Проверяем, что сообщение не будет пустым
                    client.sendMessage();
                    messageInput.setText("");
                    closeVirtualKeyboard();
                    scroll.fullScroll(View.FOCUS_DOWN);
                }
                break;
        }
    }

    @SuppressWarnings("all")
    private void closeVirtualKeyboard() {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        assert inputManager != null;
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    protected void onDestroy() {
        client.setState(ClientState.IS_LEAVING); //Перед перезапуском приложения сообщаем серверу о разъединении
        client.sendMessage();
        super.onDestroy();
    }

    @Override
    public String getEnteredText() {
        return messageInput.getText().toString();
    }

    //Добавляем новое сообщение в окно чата из основной нити
    @Override
    public void handleNewMessage(final String message) {
        Handler mainHandler = new Handler(this.getMainLooper());
        Runnable r = new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                chatWindow.setText(chatWindow.getText() + message);
                chatWindow.setText(chatWindow.getText() + "\n");
            }
        };
        mainHandler.post(r);
    }

    @Override
    public String getRegisteredChatName() {
        return registeredChatName;
    }

    @Override
    public void setRegisteredChatName(String name) {
        this.registeredChatName = name;
    }
}
