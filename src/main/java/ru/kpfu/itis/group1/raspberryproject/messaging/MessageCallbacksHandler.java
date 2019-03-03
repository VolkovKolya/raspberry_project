package ru.kpfu.itis.group1.raspberryproject.messaging;

import java.util.ArrayList;
import java.util.List;

/**
 * Class hold all callbacks that will run, after incomingMessage
 */
public class MessageCallbacksHandler {
    private static List<MessageCallback> messageCallbacks = new ArrayList<>();

    public static List<MessageCallback> getMessageCallbacks() {
        return messageCallbacks;
    }

    public static void addMessageCallback(MessageCallback callback) {
        messageCallbacks.add(callback);
    }
}
