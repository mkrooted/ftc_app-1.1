package org.firstinspires.ftc.teamcode.Release;

import android.app.Activity;
import android.util.Log;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.R;
import org.firstinspires.ftc.teamcode.TeamUA_Library.Constants;
import org.firstinspires.ftc.teamcode.TeamUA_Library.Controls.Axis;
import org.firstinspires.ftc.teamcode.TeamUA_Library.Controls.AxisMiddlewares.ConditionalMiddleware;
import org.firstinspires.ftc.teamcode.TeamUA_Library.Controls.AxisMiddlewares.InverseMiddleware;
import org.firstinspires.ftc.teamcode.TeamUA_Library.Controls.AxisMiddlewares.TriggerMiddleware;
import org.firstinspires.ftc.teamcode.TeamUA_Library.Controls.GamepadHandler;
import org.firstinspires.ftc.teamcode.TeamUA_Library.Controls.SwitchStateListener;
import org.firstinspires.ftc.teamcode.TeamUA_Library.Controls.Key;
import org.firstinspires.ftc.teamcode.TeamUA_Library.General;

@TeleOp(name = "TEAM UA - VERY FINAL RELEASE", group = "Release")
public class TeamUA extends LinearOpMode {
    private PatrioticHarvesterControls controlsManager;
    private PatrioticHarvesterHardware hardwareManager;
    private ElapsedTime period;

    private boolean sorter_override = true;
    private boolean last_barrel_direction = false;
//    private boolean elevator_speed_down = false;

    private boolean is_braking = false;
    private boolean are_controls_reversed = false;

    private final static double ELEVATOR_SPEED_NORMAL = 0.6;
    private final static double ELEVATOR_SPEED_SMALL = 0.2;

    private GamepadHandler.SwitchInput frontElevatorSwitch, rearElevatorSwitch;

    private String TAG = "TEAM_UA";
    private double last_log_time;
    private final static long LOGGING_INTERVAL = 2500; // ms
    private InverseMiddleware blue_container_condition;

    @Override
    public void runOpMode() throws InterruptedException {
        // Initializing
        my_init();

        //Starting
        my_start();

        //Main loop
        while (opModeIsActive()) {
            my_loop();
        }

        //Stopping
        my_stop();
    }

    private void my_init() {
        controlsManager = new PatrioticHarvesterControls(this);
        hardwareManager = new PatrioticHarvesterHardware(this, controlsManager);

        hardwareManager.init();
        controlsManager.init();

        period = new ElapsedTime();

        Log.i(TAG, "SYSTEM INIT FINISHED");
    }

    private void my_start() throws InterruptedException {
        controlsManager.start();
        hardwareManager.start();

        controlsManager.DriverGamepad.addButtonLink(Key.Y, true, hardwareManager.harvester_dc);
        frontElevatorSwitch = (GamepadHandler.SwitchInput) controlsManager.SupportGamepad.addButtonLink(Key.A, true, hardwareManager.front_elevator_dc, ELEVATOR_SPEED_NORMAL);
        rearElevatorSwitch = (GamepadHandler.SwitchInput) controlsManager.SupportGamepad.addButtonLink(Key.A, true, hardwareManager.rear_elevator_dc, ELEVATOR_SPEED_NORMAL);

        blue_container_condition = new InverseMiddleware(false, null);
        controlsManager.SupportGamepad.addAxisLink(Axis.RIGHT_TRIGGER, hardwareManager.blue_container_dc, new TriggerMiddleware(false, blue_container_condition));

        controlsManager.red_gate_switch.addEventListener(new SwitchStateListener() {
            @Override
            public void onSwitchStateChanged(boolean new_switch_state) {
                if (new_switch_state) {
                    hardwareManager.red_gate.open();
                } else {
                    hardwareManager.red_gate.close();
                }
            }
        });
        controlsManager.blue_gate_switch.addEventListener(new SwitchStateListener() {
            @Override
            public void onSwitchStateChanged(boolean new_switch_state) {
                if (new_switch_state) {
                    hardwareManager.blue_gate.open();
                    blue_container_condition.set_state(false);
                } else {
                    hardwareManager.blue_gate.close();
                    blue_container_condition.set_state(true);
                }
            }
        });

        telemetry.addData("System", "SYSTEM ONLINE");
        Log.i(TAG, "ROBOT ONLINE");
        last_log_time = period.milliseconds();

        General.waitForTick(period, 5);
    }

    private void my_loop() {
        boolean logging = false;
        if (period.milliseconds() - last_log_time >= LOGGING_INTERVAL) {
            logging = true;
            last_log_time = period.milliseconds();
        }

        telemetry.addData("Hoist", "Position - " + hardwareManager.hoist_dc.getCurrentPosition());

        if (logging) Log.i(TAG, "IN LOOP");

        controlsManager.update();
        hardwareManager.update();
        if (logging) Log.i(TAG, ">  peripherals update");

        handleControls();
        if (logging) Log.i(TAG, ">  controls handling");

        handleHarvestDirection();
        if (logging) Log.i(TAG, ">  harvester handling");

        handleHoistControl();
        if (logging) Log.i(TAG, ">  hoist handling");

        handleSorter();
        if (logging) Log.i(TAG, ">  sorter handling");

        handleBrake();
        if (logging) Log.i(TAG, ">  brake handling\n>  LOOP END");

        idle();
    }

    private void my_stop() {
        controlsManager.stop();
        hardwareManager.stop();

        telemetry.addData("System", "SYSTEM OFFLINE");
        Log.i(TAG, "ROBOT STOPPED");
    }

    private void handleControls() {
        if (controlsManager.reverse_controls_switch.isActivated() != are_controls_reversed) {
            are_controls_reversed = controlsManager.reverse_controls_switch.isActivated();
            if (are_controls_reversed) {
                hardwareManager.right_wheel_dc.setDirection(DcMotorSimple.Direction.FORWARD);
                hardwareManager.left_wheel_dc.setDirection(DcMotorSimple.Direction.REVERSE);
            } else {
                hardwareManager.left_wheel_dc.setDirection(DcMotorSimple.Direction.FORWARD);
                hardwareManager.right_wheel_dc.setDirection(DcMotorSimple.Direction.REVERSE);
            }
        }

        if (are_controls_reversed) {
            hardwareManager.left_wheel_dc.setPower(gamepad1.right_stick_y);
            hardwareManager.right_wheel_dc.setPower(gamepad1.left_stick_y);
        } else {
            hardwareManager.left_wheel_dc.setPower(gamepad1.left_stick_y);
            hardwareManager.right_wheel_dc.setPower(gamepad1.right_stick_y);
        }
    }

    private void handleBrake() {
        if (controlsManager.brake_button.isPressed() != is_braking) {
            is_braking = controlsManager.brake_button.isPressed();
            if (is_braking) {
                hardwareManager.right_wheel_dc.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                hardwareManager.left_wheel_dc.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            } else {
                hardwareManager.right_wheel_dc.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
                hardwareManager.left_wheel_dc.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            }
        }
    }

    private void handleHoistControl() {
        hardwareManager.hoist.update();
        if (controlsManager.hoist_up_switch.isActivated()) {
            controlsManager.hoist_down_switch.setState(false);
            hardwareManager.hoist.ascend();
        } else if (controlsManager.hoist_down_switch.isActivated()) {
            controlsManager.hoist_up_switch.setState(false);
            hardwareManager.hoist.descend();
        } else {
            hardwareManager.hoist.stop();
        }
        telemetry.addData("Hoist switch", "UP - " + controlsManager.hoist_up_switch.isActivated() + "; DOWN - " + controlsManager.hoist_down_switch.isActivated());
    }

    private void handleHarvestDirection() {
        if (last_barrel_direction != controlsManager.reverse_harvest_switch.isActivated()) {
            last_barrel_direction = controlsManager.reverse_harvest_switch.isActivated();
            if (last_barrel_direction) {
                hardwareManager.harvester_dc.setDirection(DcMotorSimple.Direction.FORWARD);
            } else {
                hardwareManager.harvester_dc.setDirection(DcMotorSimple.Direction.REVERSE);
            }
        }
    }

    private void handleSorter() {
        if (sorter_override != controlsManager.sorter_override_switch.isActivated()) {
            sorter_override = controlsManager.sorter_override_switch.isActivated();
            if (sorter_override) {
//                hardwareManager.elevatorStopSensor.clearEventListeners();
//                if (elevator_speed_down) {
//                    elevator_speed_down = false;
//                    rearElevatorSwitch.setNewValue(ELEVATOR_SPEED_NORMAL);
//                    frontElevatorSwitch.setNewValue(ELEVATOR_SPEED_NORMAL);
//                }
                controlsManager.SupportGamepad.removeButtonLink(hardwareManager.front_elevator_dc);
                controlsManager.SupportGamepad.removeButtonLink(hardwareManager.rear_elevator_dc);
                controlsManager.SupportGamepad.addButtonLink(Key.A, false, hardwareManager.front_elevator_dc, ELEVATOR_SPEED_NORMAL);
                controlsManager.SupportGamepad.addButtonLink(Key.A, false, hardwareManager.rear_elevator_dc, ELEVATOR_SPEED_NORMAL);
            } else {
                controlsManager.SupportGamepad.removeButtonLink(hardwareManager.front_elevator_dc);
                controlsManager.SupportGamepad.removeButtonLink(hardwareManager.rear_elevator_dc);
                frontElevatorSwitch = (GamepadHandler.SwitchInput) controlsManager.SupportGamepad.addButtonLink(Key.A, true, hardwareManager.front_elevator_dc, ELEVATOR_SPEED_NORMAL);
                rearElevatorSwitch = (GamepadHandler.SwitchInput) controlsManager.SupportGamepad.addButtonLink(Key.A, true, hardwareManager.rear_elevator_dc, ELEVATOR_SPEED_NORMAL);
            }
        }
        if (sorter_override) {
            telemetry.addData("Sorter override", "OVERRIDEN - press DPAD LEFT to exit");
            if (controlsManager.blue_button.isPressed())
                hardwareManager.sorter_servo.setPosition(Constants.SORTER_BLUE_POS);
            else if (controlsManager.red_button.isPressed())
                hardwareManager.sorter_servo.setPosition(Constants.SORTER_RED_POS);
            else {
                hardwareManager.sorter_servo.setPosition(Constants.SORTER_NULL_POS);
            }
        } else {
            telemetry.addData("Sorter override", "press DPAD LEFT to override");
            hardwareManager.ball_sorter.update();
//            double distance = hardwareManager.elevatorStopSensor.update();

//            if (hardwareManager.elevatorStopSensor.inRange() && hardwareManager.ball_sorter.getCurrentBall() != Sorter.Ball.NONE) {
//                if (!elevator_speed_down) {
//                    elevator_speed_down = true;
//                    rearElevatorSwitch.setNewValue(ELEVATOR_SPEED_SMALL);
//                    frontElevatorSwitch.setNewValue(ELEVATOR_SPEED_SMALL);
//                }
//            } else {
//                if (elevator_speed_down) {
//                    elevator_speed_down = false;
//                    rearElevatorSwitch.setNewValue(ELEVATOR_SPEED_NORMAL);
//                    frontElevatorSwitch.setNewValue(ELEVATOR_SPEED_NORMAL);
//                }
//            }

            telemetry.addData("BALL", hardwareManager.ball_sorter.getCurrentBall());
//            telemetry.addData("STOP SENSOR", "Distance: " + distance + "; Ball in range: " + hardwareManager.elevatorStopSensor.inRange());
        }
    }

    private void setColor(final int color) {
        final Activity activity = ((Activity) hardwareMap.appContext);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                int id = activity.getResources().getIdentifier("top", "id", activity.getPackageName());
                activity.findViewById(R.id.top_bar).setBackgroundColor(color);
            }
        });
    }
}
