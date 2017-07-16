package org.firstinspires.ftc.teamcode.Release;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.TeamUA_Library.Controls.GamepadHandler;
import org.firstinspires.ftc.teamcode.TeamUA_Library.Controls.Key;
import org.firstinspires.ftc.teamcode.TeamUA_Library.Managers.ControlsManager;

class PatrioticHarvesterControls implements ControlsManager {
    GamepadHandler.SwitchInput red_gate_switch;  // B
    GamepadHandler.SwitchInput blue_gate_switch; // X
    GamepadHandler.SwitchInput reverse_harvest_switch; // DPAD_RIGHT
    GamepadHandler.SwitchInput hoist_up_switch;  // DPAD_UP
    GamepadHandler.SwitchInput hoist_down_switch;  // DPAD_DOWN
    GamepadHandler.SwitchInput sorter_override_switch; // DPAD_LEFT

    GamepadHandler.ButtonInput blue_button; // LEFT BUMPER
    GamepadHandler.ButtonInput red_button; // RIGHT BUMPER
    GamepadHandler.ButtonInput brake_button; // RIGHT STICK

    GamepadHandler.SwitchInput reverse_controls_switch; // A

    GamepadHandler.AxisInput blue_pusher_axis; // Right trigger

    GamepadHandler DriverGamepad;
    GamepadHandler SupportGamepad;

    PatrioticHarvesterControls(OpMode opmode) {
        DriverGamepad = new GamepadHandler(opmode.gamepad1);
        SupportGamepad = new GamepadHandler(opmode.gamepad2);
    }

    @Override
    public void init() {
        sorter_override_switch = SupportGamepad.addSwitch(Key.DPAD_LEFT, false);
        hoist_up_switch = SupportGamepad.addSwitch(Key.DPAD_UP, false);
        hoist_down_switch = SupportGamepad.addSwitch(Key.DPAD_DOWN, false);
        reverse_harvest_switch = DriverGamepad.addSwitch(Key.DPAD_RIGHT, false);

        blue_gate_switch = SupportGamepad.addSwitch(Key.X, false);
        red_gate_switch = SupportGamepad.addSwitch(Key.B, false);

        blue_button = SupportGamepad.getButton(Key.LEFT_BUTTON);
        red_button = SupportGamepad.getButton(Key.RIGHT_BUTTON);

        brake_button = DriverGamepad.addSwitch(Key.LEFT_BUTTON);
        reverse_controls_switch = DriverGamepad.addSwitch(Key.RIGHT_BUTTON);
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public void update() {
        DriverGamepad.update();
        SupportGamepad.update();
    }
}
