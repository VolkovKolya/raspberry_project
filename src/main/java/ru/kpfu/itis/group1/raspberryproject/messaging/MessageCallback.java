package ru.kpfu.itis.group1.raspberryproject.messaging;

import org.eclipse.paho.client.mqttv3.MqttMessage;

@FunctionalInterface
public interface MessageCallback {
    void processMessage(String topic, MqttMessage message);
}
