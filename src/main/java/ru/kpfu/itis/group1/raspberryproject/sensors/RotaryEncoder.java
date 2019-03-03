package ru.kpfu.itis.group1.raspberryproject.sensors;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import ru.kpfu.itis.group1.raspberryproject.configs.GpioControllerSingleton;

import java.util.Collections;
import java.util.List;


public class RotaryEncoder {

    private final GpioPinDigitalInput CLK;
    private final GpioPinDigitalInput DT;
    private final GpioPinDigitalInput SW;

    private List<Runnable> clockwiseTasks = Collections.emptyList();
    private List<Runnable> antiClockwiseTasks = Collections.emptyList();
    private List<Runnable> rotationTasks = Collections.emptyList();
    private List<Runnable> pushButtonTasks = Collections.emptyList();

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

        addRotationListener();
        addButtonListener();
    }

    /*
     * This method add listener to RotaryEncoder  with next logic:
     * All pushButton tasks will be run after push button
     */
    private void addButtonListener() {
        SW.addListener((GpioPinListenerDigital) event -> {
            if (event.getState() == PinState.HIGH) {
                pushButtonTasks.forEach(Runnable::run);
            }
        });
    }

    /*
     * This method add listener to RotaryEncoder  with next logic:
     * All clockwise tasks will be run after clockwise rotation
     * All anticlockwise tasks will be run after anti clockwise rotation
     * All rotation tasks will be run after any rotation
     */
    private void addRotationListener() {
        CLK.addListener((GpioPinListenerDigital) event -> {
            final PinState newState = event.getState();

            if (newState != PinsLastStateHolder.lastClkState) {
                if (DT.getState() != newState) {
                    antiClockwiseTasks.forEach(Runnable::run);
                }
                else{
                    clockwiseTasks.forEach(Runnable::run);
                }
                PinsLastStateHolder.lastClkState = newState;
            }
            rotationTasks.forEach(Runnable::run);
        });
    }


    /**
     * Runnable will be run, after any rotation
     *
     * @param runnable
     */
    public void addTaskOnRotation(final Runnable runnable) {
        rotationTasks.add(runnable);
    }

    /**
     * Runnable will be run, after push button rotary encoder
     *
     * @param runnable
     */
    public void addTaskOnPushButton(final Runnable runnable) {
        pushButtonTasks.add(runnable);
    }

    /**
     * Runnable will be run, after clockwise rotation
     *
     * @param runnable
     */
    public void addTaskOnClockwiseRotation(final Runnable runnable) {
        clockwiseTasks.add(runnable);
    }


    /**
     * Runnable will be run, after antiClockwise rotation
     *
     * @param runnable
     */
    public void addTaskOnAntiClockwiseRotation(final Runnable runnable) {
        antiClockwiseTasks.add(runnable);
    }

    public void removeTasks() {
        clockwiseTasks.clear();
        antiClockwiseTasks.clear();
        rotationTasks.clear();
    }

    private static class PinsLastStateHolder {
        private static PinState lastClkState;
    }

}
