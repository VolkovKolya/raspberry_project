package ru.kpfu.itis.group1.raspberryproject.sensors;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.gpio.trigger.GpioCallbackTrigger;
import ru.kpfu.itis.group1.raspberryproject.configs.GpioControllerSingleton;


public class RotaryEncoder {

    private final GpioPinDigitalInput CLK;
    private final GpioPinDigitalInput DT;
    private final GpioPinDigitalInput SW;

    public RotaryEncoder(final String clkPinName,
                         final String dtPinName,
                         final String swPinName) {

        final GpioController gpio = GpioControllerSingleton.getGpioController();
        CLK = gpio.provisionDigitalInputPin(RaspiPin.getPinByName(clkPinName),
                PinPullResistance.PULL_DOWN);
        DT = gpio.provisionDigitalInputPin(RaspiPin.getPinByName(dtPinName),
                PinPullResistance.PULL_DOWN);
        SW = gpio.provisionDigitalInputPin(RaspiPin.getPinByName(swPinName),
                PinPullResistance.PULL_DOWN);

        CLK.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        DT.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        SW.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
    }


    /**
     * Runnable will be run, after any rotation
     *
     * @param runnable
     */
    public void addTriggerOnRotation(final Runnable runnable) {
        CLK.addTrigger(new GpioCallbackTrigger(() -> {
            runnable.run();
            return null;
        }));
    }

    /**
     * Runnable will be run, after push button rotary encoder
     *
     * @param runnable
     */
    public void addTriggerOnAction(final Runnable runnable) {
        SW.addTrigger(new GpioCallbackTrigger(() -> {
            runnable.run();
            return null;
        }));
    }

    /**
     * Runnable will be run, after clockwise rotation
     *
     * @param runnable
     */
    public void addTriggerOnClockwiseRotation(final Runnable runnable) {
        DT.addListener((GpioPinListenerDigital) event -> {

            final PinState newState = event.getState();
            if (newState != PinsLastStateHolder.lastDtState) {
                if (CLK.getState() != newState) {
                    runnable.run();
                }
                PinsLastStateHolder.lastDtState = newState;
            }
        });
    }


    /**
     * Runnable will be run, after anti clockwise rotation
     *
     * @param runnable
     */
    public void addTriggerOnAntiClockwiseRotation(final Runnable runnable) {
        CLK.addListener((GpioPinListenerDigital) event -> {

            final PinState newState = event.getState();
            if (newState != PinsLastStateHolder.lastClkState) {
                if (DT.getState() != newState) {
                    runnable.run();
                }
                PinsLastStateHolder.lastClkState = newState;
            }
        });
    }

    public void removeListeners() {
        CLK.removeAllListeners();
        DT.removeAllListeners();
        SW.removeAllListeners();
    }

    public void removeTrigers() {
        CLK.removeAllTriggers();
        DT.removeAllTriggers();
        SW.removeAllTriggers();
    }

    private static class PinsLastStateHolder {
        private static PinState lastClkState;
        private static PinState lastDtState;
    }

}
