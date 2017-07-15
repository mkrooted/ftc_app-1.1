package org.firstinspires.ftc.teamcode.TeamUA_Library.Controls;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import org.firstinspires.ftc.teamcode.TeamUA_Library.Controls.AxisMiddlewares.AxisMiddleware;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by mkrooted on 30.05.2017.
 */

public class GamepadHandler {
    private Gamepad gamepad;
    private HashSet<SwitchInput> switches;
    private HashMap<DcMotorSimple, AxisInput> axisLinks;
    private HashMap<DcMotorSimple, ButtonInput> buttonLinks;
    private HashSet<DcMotorSimple> pausedButtonLinks;

    private Timer update_loop;

    public GamepadHandler(Gamepad gamepad) {
        this.gamepad = gamepad;

        switches = new HashSet<>();
        axisLinks = new HashMap<>();
        buttonLinks = new HashMap<>();
        pausedButtonLinks = new HashSet<>();
    }

    public void start(long update_period) {
        update_loop = new Timer();
        update_loop.scheduleAtFixedRate(
            new TimerTask() {
                @Override
                public void run() {
                    update();
                }
            },
            0,
            update_period
        );
    }

    public void stop() {
        update_loop.cancel();
    }

    public void update() {
        for (SwitchInput s : switches)
            s.update();
        for (Map.Entry<DcMotorSimple, AxisInput> i : axisLinks.entrySet())
            i.getKey().setPower(i.getValue().getValue());
        for (Map.Entry<DcMotorSimple, ButtonInput> i : buttonLinks.entrySet()){
            DcMotorSimple device = i.getKey();
            if (!pausedButtonLinks.contains(device))
                device.setPower(i.getValue().getValue());
        }
    }

    // General
    public boolean isPressed(int key) {
        switch (key) {
            case Key.A:
                return gamepad.a;
            case Key.B:
                return gamepad.b;
            case Key.X:
                return gamepad.x;
            case Key.Y:
                return gamepad.y;
            case Key.DPAD_UP:
                return gamepad.dpad_up;
            case Key.DPAD_LEFT:
                return gamepad.dpad_left;
            case Key.DPAD_DOWN:
                return gamepad.dpad_down;
            case Key.DPAD_RIGHT:
                return gamepad.dpad_right;
            case Key.LEFT_BUTTON:
                return gamepad.left_bumper;
            case Key.RIGHT_BUTTON:
                return gamepad.right_bumper;
            case Key.LEFT_STICK_BTN:
                return gamepad.left_stick_button;
            case Key.RIGHT_STICK_BTN:
                return gamepad.right_stick_button;
        }
        return false;
    }

    public double getAxisValue(int axis) {
        double output = 0d;
        switch (axis) {
            case Axis.LEFT_STICK_X:
                output = gamepad.left_stick_x;
                break;
            case Axis.RIGHT_STICK_X:
                output = gamepad.right_stick_x;
                break;
            case Axis.LEFT_STICK_Y:
                output = gamepad.left_stick_y;
                break;
            case Axis.RIGHT_STICK_Y:
                output = gamepad.right_stick_y;
                break;
            case Axis.LEFT_TRIGGER:
                output = gamepad.left_trigger;
                break;
            case Axis.RIGHT_TRIGGER:
                output = gamepad.right_trigger;
                break;
        }
        return output;
    }

    public ButtonInput getButton(int button) {
        return new ButtonInput(button);
    }

    // Axis Links
    public AxisInput addAxisLink(int axis, DcMotorSimple device) {
        return addAxisLink(axis, device, null);
    }

    public AxisInput addAxisLink(int axis, DcMotorSimple device, AxisMiddleware middleware) {
        AxisInput input = null;
        if (Axis.is_valid(axis)) {
            input = new AxisInput(axis, middleware);
            axisLinks.put(device, input);
        }
        return input;
    }

    public void removeAxisLink(DcMotorSimple device) {
        axisLinks.remove(device);
    }

    public void removeAllAxisLinks() {
        axisLinks.clear();
    }

    // Button Links
    public ButtonInput addButtonLink(int key, boolean is_switch, DcMotorSimple device) {
        ButtonInput input = null;
        if (Key.is_valid(key)) {
            if (is_switch) {
                input = addSwitch(key);
                switches.add((SwitchInput) input);
            } else {
                input = new ButtonInput(key);
            }
            buttonLinks.put(device, input);
        }
        return input;
    }

    public ButtonInput addButtonLink(int key, boolean is_switch, DcMotorSimple device, double value) {
        ButtonInput input = null;
        if (Key.is_valid(key)) {
            if (is_switch) {
                input = addSwitch(key);
                input.setNewValue(value);
                switches.add((SwitchInput) input);
            } else {
                input = new ButtonInput(key);
                input.setNewValue(value);
            }
            buttonLinks.put(device, input);
        }
        return input;
    }

    public void removeButtonLink(DcMotorSimple device) {
        buttonLinks.remove(device);
    }

    public void pauseButtonLink(DcMotorSimple device) {
        pausedButtonLinks.add(device);
    }
    public void resumeButtonLink(DcMotorSimple device) {
        pausedButtonLinks.remove(device);
    }

    public void removeAllButtonLinks() {
        buttonLinks.clear();
    }

    // Switches
    public SwitchInput addSwitch(int key) {
        return addSwitch(key, false);
    }

    public SwitchInput addSwitch(int key, boolean normal) {
        if (Key.is_valid(key)) {
            SwitchInput input = new SwitchInput(key, normal);
            switches.add(input);
            return input;
        }
        return null;
    }

    public void removeSwitch(SwitchInput switchInput) {
        switches.remove(switchInput);
    }

    public void removeAllSwitches() {
        switches.clear();
    }

    // Input
    public interface Input {
        double getValue();
    }

    // Button Input
    public class ButtonInput implements Input {
        protected int key;
        double ADCValue;

        public ButtonInput(int key) {
            this(key, 1d);
        }

        public ButtonInput(int key, double value) {
            if (!Key.is_valid(key))
                throw new IllegalArgumentException("Invalid Key ID: " + key);
            this.key = key;
            ADCValue = value;
        }

        public boolean isPressed() {
            switch (key) {
                case Key.A:
                    return gamepad.a;
                case Key.B:
                    return gamepad.b;
                case Key.X:
                    return gamepad.x;
                case Key.Y:
                    return gamepad.y;
                case Key.DPAD_UP:
                    return gamepad.dpad_up;
                case Key.DPAD_LEFT:
                    return gamepad.dpad_left;
                case Key.DPAD_DOWN:
                    return gamepad.dpad_down;
                case Key.DPAD_RIGHT:
                    return gamepad.dpad_right;
                case Key.LEFT_BUTTON:
                    return gamepad.left_bumper;
                case Key.RIGHT_BUTTON:
                    return gamepad.right_bumper;
                case Key.LEFT_STICK_BTN:
                    return gamepad.left_stick_button;
                case Key.RIGHT_STICK_BTN:
                    return gamepad.right_stick_button;
            }
            return false;
        }

        public double getValue() {
            return isPressed() ? ADCValue : 0;
        }

        public void setNewValue(double value) {
            ADCValue = value;
        }
    }

    // Switch Input
    public class SwitchInput extends ButtonInput {
        private boolean switch_state;
        private boolean last_button_state;

        private ArrayList<SwitchStateListener> switchStateListenerList;

        public SwitchInput(int key) {
            this(key, false);
        }

        public SwitchInput(int key, boolean normal) {
            super(key);
            switch_state = normal;
            switchStateListenerList = new ArrayList<>();
        }

        public void update() {
            boolean button_state = isPressed();
            if (last_button_state != button_state) {
                last_button_state = button_state;
                if (last_button_state)
                    switch_state = !switch_state;
                    for( SwitchStateListener listener : switchStateListenerList) {
                        listener.onSwitchStateChanged(switch_state);
                    }
            }
        }

        public void addEventListener(SwitchStateListener event) {
            switchStateListenerList.add(event);
        }

        public boolean isActivated() {
            return switch_state;
        }

        public double getValue() {
            return switch_state ? ADCValue : 0d;
        }

        public void setState(boolean is_active) {
            switch_state = is_active;
        }
        public void manual_switch() {
            switch_state = !switch_state;
        }
    }

    // Axis Input
    public class AxisInput implements Input {
        private int axis;
        private AxisMiddleware mid;

        public AxisInput(int axis) {
            this(axis, null);
        }

        public AxisInput(int axis, AxisMiddleware middleware) {
            if (!Axis.is_valid(axis))
                throw new IllegalArgumentException("Invalid Axis ID: " + axis);
            this.axis = axis;
            mid = middleware;
        }

        public double getValue() {
            double output = 0d;
            switch (axis) {
                case Axis.LEFT_STICK_X:
                    output = gamepad.left_stick_x;
                    break;
                case Axis.RIGHT_STICK_X:
                    output = gamepad.right_stick_x;
                    break;
                case Axis.LEFT_STICK_Y:
                    output = gamepad.left_stick_y;
                    break;
                case Axis.RIGHT_STICK_Y:
                    output = gamepad.right_stick_y;
                    break;
                case Axis.LEFT_TRIGGER:
                    output = gamepad.left_trigger;
                    break;
                case Axis.RIGHT_TRIGGER:
                    output = gamepad.right_trigger;
                    break;
            }
            if (mid == null)
                return output;
            else
                return mid.forward(output);
        }
    }
}

