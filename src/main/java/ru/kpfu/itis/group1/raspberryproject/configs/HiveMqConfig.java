package ru.kpfu.itis.group1.raspberryproject.configs;

import org.eclipse.paho.client.mqttv3.*;
import ru.kpfu.itis.group1.raspberryproject.messaging.MessageCallbacksHandler;

public class HiveMqConfig {

    private static MqttClient client;

    static {
        connect();
        setCallback();
    }

    private static void connect() {
        String uri = PropertiesHolder.getProperties().getProperty("hive-mq.uri");
        try {
            client = new MqttClient(
                    uri,
                    MqttClient.generateClientId());
            client.connect();
        } catch (MqttException e) {
            System.out.println("Can't connect to broker");
            throw new IllegalStateException("Can't connect to broker");
        }
    }

    private static void setCallback() {
        client.setCallback(new MqttCallback() {

            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("Connection with broker lost");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                MessageCallbacksHandler.getMessageCallbacks()
                        .forEach(callback -> callback.processMessage(topic, message));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });
    }


    public static MqttClient getClient() {
        return client;
    }
}
