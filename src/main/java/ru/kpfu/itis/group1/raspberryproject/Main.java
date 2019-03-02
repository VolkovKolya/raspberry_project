package ru.kpfu.itis.group1.raspberryproject;


import ru.kpfu.itis.group1.raspberryproject.sensors.LaserEmitter;
import ru.kpfu.itis.group1.raspberryproject.sensors.RotaryEncoder;

import java.util.Properties;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Properties prop = new Properties();
        try {
            prop.load(Main.class.getClassLoader().getResourceAsStream("application.properties"));
        } catch (Exception e) {
            System.out.println("Can't load properties");
            throw new IllegalStateException(e.getMessage());
        }


        final RotaryEncoder rotaryEncoder = new RotaryEncoder(
                prop.getProperty("rotary-encoder.clk-pin"),
                prop.getProperty("rotary-encoder.dt-pin"),
                prop.getProperty("rotary-encoder.sw-pin")
        );

        final LaserEmitter laserEmitter = new LaserEmitter(
                prop.getProperty("laser-emitter.pin")
        );



        rotaryEncoder.addTriggerOnRotation(() -> {
            laserEmitter.turnOn(2000);
        });

        rotaryEncoder.addTriggerOnPushButton(laserEmitter::inverseState);

        rotaryEncoder.addTriggerOnClockwiseRotation(() -> {
            System.out.println("Rotary encoder is spinning clockwise");
        });

        rotaryEncoder.addTriggerOnAntiClockwiseRotation(() -> {
            System.out.println("Rotary encoder is spinning counterclockwise");
        });

        System.out.println("The program is running");
        System.out.println("You can use the CTRL-C keystroke to terminate program at any time.");
        while (true) {
            Thread.sleep(100000);
        }


    }
}
