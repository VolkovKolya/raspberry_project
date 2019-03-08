package ru.kpfu.itis.group1.raspberryproject.sensors;

import com.pi4j.io.gpio.*;
import ru.kpfu.itis.group1.raspberryproject.configs.GpioControllerSingleton;

public class LaserEmitter {
    private final GpioPinDigitalOutput PIN;
    private final GpioPinPwmOutput PWM_PIN;

    public LaserEmitter(final String pinName,final String pwmPinName) {
        final GpioController gpio = GpioControllerSingleton.getGpioController();

        PIN = gpio.provisionDigitalOutputPin(RaspiPin.getPinByName(pinName), PinState.LOW);
        PWM_PIN = gpio.provisionPwmOutputPin(RaspiPin.getPinByName(pwmPinName));

        PIN.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        PWM_PIN.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
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

    public void inverseState() {
        PIN.toggle();
    }

    /**
     * Increase by 10 pwm value of pin.
     * Max value - 100.
     */
    public void incPwm() {
        final int currentValue = PWM_PIN.getPwm();
        PWM_PIN.setPwm(Math.min(100, currentValue + 10));
    }

    /**
     * Decrease by 10 pwm value of pin.
     * Min value - 0.
     */
    public void decPwm() {
        final int currentValue = PWM_PIN.getPwm();
        PWM_PIN.setPwm(Math.max(0, currentValue - 10));
    }

}
