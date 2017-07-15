package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.qualcomm.hardware.lynx.LynxI2cColorRangeSensor;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.TeamUA_Library.Constants;
import org.firstinspires.ftc.teamcode.TeamUA_Library.Controls.Axis;
import org.firstinspires.ftc.teamcode.TeamUA_Library.Controls.AxisMiddlewares.LinearInterpolationMiddleware;
import org.firstinspires.ftc.teamcode.TeamUA_Library.Controls.GamepadHandler;
import org.firstinspires.ftc.teamcode.TeamUA_Library.Wrappers.BallGate;
import org.firstinspires.ftc.teamcode.TeamUA_Library.Controls.Key;
import org.firstinspires.ftc.teamcode.TeamUA_Library.General;
import org.firstinspires.ftc.teamcode.TeamUA_Library.Wrappers.Elevator;
import org.firstinspires.ftc.teamcode.TeamUA_Library.Wrappers.Hoist;
import org.firstinspires.ftc.teamcode.TeamUA_Library.Wrappers.Sorter;

/**
 * Created by mkrooted on 30.05.2017.
 */

@TeleOp(name = "BETA: General", group = "Beta Version")
public class Beta_OpMode extends OpMode {
    private DcMotor hoist_dc;
    private DcMotor harvester_dc;
    private DcMotor rear_elevator_dc;
    private DcMotor front_elevator_dc;
    private DcMotor left_wheel_dc;
    private DcMotor right_wheel_dc;
    private Servo sorter_servo;
    private LynxI2cColorRangeSensor color_sensor;

    private Sorter ball_sorter;
    private BallGate blue_gate;
    private BallGate red_gate;

    private GamepadHandler gamepadHandler;

    private GamepadHandler.SwitchInput elevator_switch;  // A
    private GamepadHandler.SwitchInput harvest_switch;   // Y
    private GamepadHandler.SwitchInput red_gate_switch;  // B
    private GamepadHandler.SwitchInput blue_gate_switch; // X
    private GamepadHandler.SwitchInput reverse_harvest_switch; // DPAD_RIGHT
    private GamepadHandler.SwitchInput hoist_up_switch;  // DPAD_UP
    private GamepadHandler.SwitchInput hoist_down_switch;  // DPAD_DOWN
    private GamepadHandler.SwitchInput sorter_override_switch; // DPAD_LEFT

    private GamepadHandler.ButtonInput blue_button; // LEFT BUMPER
    private GamepadHandler.ButtonInput red_button; // RIGHT BUMPER

    // RIGHT STICK
    private GamepadHandler.AxisInput hoist_axis;
    private GamepadHandler.AxisInput left_wheel_axis;
    // LEFT STICK
    private GamepadHandler.AxisInput right_wheel_axis;

    private GamepadHandler.AxisInput claw_up_button; // LEFT TRIGGER
    private GamepadHandler.AxisInput lift_button; // RIGHT TRIGGER

    private ElapsedTime period;

    private Hoist hoist;

    private boolean last_barrel_direction = false;
    private boolean last_red_gate_state = false;
    private boolean last_blue_gate_state = false;
    private boolean sorter_override = true;
    private boolean gameStarted = false;

    @Override
    public void init() {
        gamepadHandler = new GamepadHandler(gamepad1);

        hoist_dc = hardwareMap.dcMotor.get("hoist_dc");
        hoist_dc.setDirection(DcMotorSimple.Direction.REVERSE);
        front_elevator_dc = hardwareMap.dcMotor.get("front_elevator_dc");
        rear_elevator_dc = hardwareMap.dcMotor.get("rear_elevator_dc");
        harvester_dc = hardwareMap.dcMotor.get("harvester_dc");
        left_wheel_dc = hardwareMap.dcMotor.get("left_wheel_dc");
        right_wheel_dc = hardwareMap.dcMotor.get("right_wheel_dc");
        right_wheel_dc.setDirection(DcMotorSimple.Direction.REVERSE);

        color_sensor = (LynxI2cColorRangeSensor) hardwareMap.colorSensor.get("color_sensor");

        sorter_servo = hardwareMap.servo.get("sort_servo");
        red_gate = new BallGate(hardwareMap.servo.get("red_gate"), 0, 1);
        blue_gate = new BallGate(hardwareMap.servo.get("blue_gate"), 0, 1);
        ball_sorter = new Sorter(sorter_servo, color_sensor, Constants.SORTER_RED_POS, Constants.SORTER_BLUE_POS, Constants.SORTER_NULL_POS);

        hoist = new Hoist(hoist_dc, Constants.HOIST_MAX_POS, 0, 1d);

        sorter_override_switch = gamepadHandler.addSwitch(Key.DPAD_LEFT, false);
        hoist_up_switch = gamepadHandler.addSwitch(Key.DPAD_UP, false);
        hoist_down_switch = gamepadHandler.addSwitch(Key.DPAD_DOWN, false);
        reverse_harvest_switch = gamepadHandler.addSwitch(Key.DPAD_RIGHT, false);

        blue_gate_switch = gamepadHandler.addSwitch(Key.X, false);
        red_gate_switch = gamepadHandler.addSwitch(Key.B, false);
        harvest_switch = (GamepadHandler.SwitchInput) gamepadHandler.addButtonLink(Key.Y, true, harvester_dc);
        gamepadHandler.addButtonLink(Key.A, true, front_elevator_dc);
        elevator_switch = (GamepadHandler.SwitchInput) gamepadHandler.addButtonLink(Key.A, true, rear_elevator_dc);

        blue_button = gamepadHandler.getButton(Key.LEFT_BUTTON);
        red_button = gamepadHandler.getButton(Key.RIGHT_BUTTON);

        left_wheel_axis = gamepadHandler.addAxisLink(Axis.LEFT_STICK_Y, left_wheel_dc, new LinearInterpolationMiddleware(1, 0.75));
        right_wheel_axis = gamepadHandler.addAxisLink(Axis.RIGHT_STICK_Y, right_wheel_dc, new LinearInterpolationMiddleware(1, 0.75));


        period = new ElapsedTime();

        telemetry.addData("Say", "Hello, world");
        telemetry.update();
    }

    @Override
    public void start() {
        gamepadHandler.start(20);
        ball_sorter.start(20);

        telemetry.addData("Say", "Game started!");
        telemetry.update();

        gameStarted = true;

        super.start();
    }

    @Override
    public void loop() {
        if (gameStarted) {
            telemetry.addData("Hoist", "Position - " + hoist_dc.getCurrentPosition());

            handleHarvestDirection();
            handleGates();
            handleHoistControl();
            handleSorter();

            try {
                General.waitForTick(period, 20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stop() {
        stop_movement();

        if (gameStarted) {
            ball_sorter.stop();
            gamepadHandler.stop();
        }

        gameStarted = false;

        super.stop();
    }

    private void handleHoistControl() {
        if (hoist_up_switch.isActivated()) {
            hoist_down_switch.setState(false);
            hoist.ascend();
        } else if (hoist_down_switch.isActivated()){
            hoist_up_switch.setState(false);
            hoist.descend();
        } else {
            hoist.stop();
        }
        telemetry.addData("Hoist switch", "UP - "+hoist_up_switch.isActivated()+"; DOWN - "+hoist_down_switch.isActivated());
    }

    private void handleHarvestDirection() {
        if (last_barrel_direction != reverse_harvest_switch.isActivated()) {
            last_barrel_direction = reverse_harvest_switch.isActivated();
            if (last_barrel_direction) {
                harvester_dc.setDirection(DcMotorSimple.Direction.FORWARD);
            } else {
                harvester_dc.setDirection(DcMotorSimple.Direction.REVERSE);
            }
        }
    }

    private void handleGates() {
        if (red_gate_switch.isActivated() != last_red_gate_state) {
            last_red_gate_state = red_gate_switch.isActivated();
            if (last_red_gate_state) {
                red_gate.open();
            } else {
                red_gate.close();
            }
        }
        if (blue_gate_switch.isActivated() != last_blue_gate_state) {
            last_blue_gate_state = blue_gate_switch.isActivated();
            if (last_blue_gate_state) {
                blue_gate.open();
            } else {
                blue_gate.close();
            }
        }
    }

    private void handleSorter() {
        if (sorter_override != sorter_override_switch.isActivated()) {
            sorter_override = sorter_override_switch.isActivated();
            if (sorter_override) {
                Log.d("TeamUA", "Sorter override");
                ball_sorter.stop();
            } else {
                ball_sorter.start(20);
            }
        }
        if (sorter_override) {
            telemetry.addData("Sorter override", "press DPAD LEFT to exit");
            if (blue_button.isPressed())
                sorter_servo.setPosition(Constants.SORTER_BLUE_POS);
            else if (red_button.isPressed())
                sorter_servo.setPosition(Constants.SORTER_RED_POS);
            else {
                sorter_servo.setPosition(Constants.SORTER_NULL_POS);
            }
        }
    }

    private void stop_movement() {
    }

}
