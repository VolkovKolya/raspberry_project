package ru.kpfu.itis.group1.raspberryproject.sensors;

import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.gpio.trigger.GpioCallbackTrigger;
import ru.kpfu.itis.group1.raspberryproject.configs.GpioControllerSingleton;


public class RotaryEncoder {

    private final GpioPinDigitalInput CLK;
    private final GpioPinDigitalInput DT;

    public RotaryEncoder(final String clkPinName,
                         final String DtPinName) {
        final GpioController gpio = GpioControllerSingleton.getGpioController();

        CLK = gpio.provisionDigitalInputPin(RaspiPin.getPinByName(clkPinName),
                PinPullResistance.PULL_DOWN);
        DT = gpio.provisionDigitalInputPin(RaspiPin.getPinByName(DtPinName),
                PinPullResistance.PULL_DOWN);

        CLK.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        DT.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
    }


    //Runnable will be run, after any action with rotary encoder
    public void addTriggerOnAction(final Runnable runnable){
        CLK.addTrigger(new GpioCallbackTrigger(() -> {
            runnable.run();
            return null;
        }));
    }

    //Runnable will be run, after clockwise rotation
    public void addTriggerOnClockwiseRotation(final Runnable runnable){
        CLK.addListener((GpioPinListenerDigital) event -> {
            final PinState newState = event.getState();
            if(newState != PinsLastStateHolder.lastClkState){
                if(DT.getState()!=newState){
                    runnable.run();
                }
                PinsLastStateHolder.lastClkState = newState;
            }
        });
    }

    //Runnable will be run, after anti clockwise rotation
    public void addTriggerOnAntiClockwiseRotation(final Runnable runnable){
        CLK.addListener((GpioPinListenerDigital) event -> {
            final PinState newState = event.getState();
            if(newState!= PinsLastStateHolder.lastClkState){
                if(DT.getState()==newState){
                    runnable.run();
                }
                PinsLastStateHolder.lastClkState = newState;
            }
        });
    }

    public void removeListeners(){
        CLK.removeAllListeners();
        DT.removeAllListeners();
    }

    public void removeTrigers(){
        CLK.removeAllTriggers();
        DT.removeAllTriggers();
    }

    private static class PinsLastStateHolder{
         private static PinState lastClkState;
    }

}
