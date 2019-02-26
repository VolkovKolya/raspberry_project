package ru.kpfu.itis.group1.raspberryproject.configs;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;

public class GpioControllerSingleton {

    private static final GpioController controller = GpioFactory.getInstance();

    public static GpioController getGpioController(){
        return controller;
    }

}
