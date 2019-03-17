package ru.kpfu.itis.group1.raspberryproject;


import org.eclipse.paho.client.mqttv3.*;
import ru.kpfu.itis.group1.raspberryproject.configs.HiveMqConfig;
import ru.kpfu.itis.group1.raspberryproject.configs.PropertiesHolder;
import ru.kpfu.itis.group1.raspberryproject.messaging.MessageCallbacksHandler;
import ru.kpfu.itis.group1.raspberryproject.sensors.LaserEmitter;
import ru.kpfu.itis.group1.raspberryproject.sensors.RotaryEncoder;

import java.util.Properties;

public class Main {
    public static void main(String[] args) throws Exception {

        final Properties prop = PropertiesHolder.getProperties();
        final RotaryEncoder rotaryEncoder = new RotaryEncoder(
                prop.getProperty("rotary-encoder.clk-pin"),
                prop.getProperty("rotary-encoder.dt-pin"),
                prop.getProperty("rotary-encoder.sw-pin")
        );
        final LaserEmitter laserEmitter = new LaserEmitter(
                prop.getProperty("laser-emitter.pin"),
                prop.getProperty("laser-emitter.pwm-pin")
        );


        // Add interaction tasks
        rotaryEncoder.addTaskOnPushButton(() -> {
            System.out.println("Button on rotary encoder is pushing");
            laserEmitter.inverseState();
        });
        rotaryEncoder.addTaskOnClockwiseRotation(() -> {
            System.out.println("Rotary encoder is spinning clockwise");
            laserEmitter.incPwm();
        });
        rotaryEncoder.addTaskOnAntiClockwiseRotation(() -> {
            System.out.println("Rotary encoder is spinning counterclockwise");
            laserEmitter.decPwm();
        });


        final String buttonQueue = prop.getProperty("queue.outgoing-button");
        final String rotationQueue = prop.getProperty("queue.outgoing-rotation");
        final String incomingButtonQueue = prop.getProperty("queue.incoming-button");

        MqttClient mqttClient = HiveMqConfig.getClient();
        mqttClient.subscribe(incomingButtonQueue);

        // Add messaging tasks
        rotaryEncoder.addTaskOnPushButton(() -> {
            try {
                mqttClient.publish(
                        buttonQueue,
                        "".getBytes(),
                        1,
                        false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        });
        rotaryEncoder.addTaskOnClockwiseRotation(() -> {
            try {
                mqttClient.publish(
                        rotationQueue,
                        "0".getBytes(),
                        1,
                        false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        });
        rotaryEncoder.addTaskOnAntiClockwiseRotation(() -> {
            try {
                mqttClient.publish(
                        rotationQueue,
                        "1".getBytes(),
                        1,
                        false);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        });

        //add callback that turnOn/turnOff laser after push button in app.
        MessageCallbacksHandler.addMessageCallback((topic,message)->{
            if (topic.equals(incomingButtonQueue)){
                System.out.println("Button in app is pushing");
                laserEmitter.inverseState();
            }
        });

        System.out.println("The program is running");
        System.out.println("You can use the CTRL-C keystroke to terminate program at any time.");
    }
}
