package ru.kpfu.itis.group1.raspberryproject.sensors;

import com.pi4j.io.gpio.*;
import ru.kpfu.itis.group1.raspberryproject.configs.GpioControllerSingleton;

public class LaserEmitter {
    private final GpioPinDigitalOutput PIN;

    public LaserEmitter(final String pinName) {
        final GpioController gpio = GpioControllerSingleton.getGpioController();

        PIN = gpio.provisionDigitalOutputPin(RaspiPin.getPinByName(pinName), PinState.LOW);
        PIN.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
    }

    public void turnOn() {
        PIN.setState(PinState.HIGH);
    }

    public void turnOn(long milliseconds) {
        PIN.pulse(milliseconds);
    }

    public void turnOff() {
        PIN.setState(PinState.LOW);
    }

}
