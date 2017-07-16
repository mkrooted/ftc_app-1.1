package org.firstinspires.ftc.teamcode.Release;

import com.qualcomm.hardware.lynx.LynxI2cColorRangeSensor;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.TeamUA_Library.Managers.ControlsManager;
import org.firstinspires.ftc.teamcode.TeamUA_Library.Wrappers.BallGate;
import org.firstinspires.ftc.teamcode.TeamUA_Library.Constants;
import org.firstinspires.ftc.teamcode.TeamUA_Library.Managers.HardwareManager;
import org.firstinspires.ftc.teamcode.TeamUA_Library.Wrappers.Elevator;
import org.firstinspires.ftc.teamcode.TeamUA_Library.Wrappers.ElevatorStopSensor;
import org.firstinspires.ftc.teamcode.TeamUA_Library.Wrappers.Hoist;
import org.firstinspires.ftc.teamcode.TeamUA_Library.Wrappers.Sorter;

class PatrioticHarvesterHardware implements HardwareManager {
    DcMotor hoist_dc;
    DcMotor harvester_dc;
    DcMotor rear_elevator_dc;
    DcMotor front_elevator_dc;
    DcMotor left_wheel_dc;
    DcMotor right_wheel_dc;
    Hoist hoist;
    Servo sorter_servo;
    Sorter ball_sorter;
    BallGate blue_gate;
    BallGate red_gate;
    DcMotor blue_container_dc;
    LynxI2cColorRangeSensor color_sensor;

    private PatrioticHarvesterControls controlsManager;
    private OpMode opmode;
    private HardwareMap hardwareMap;

    PatrioticHarvesterHardware(OpMode opmode, PatrioticHarvesterControls controlsManager) {
        this.opmode = opmode;
        this.controlsManager = controlsManager;
        hardwareMap = opmode.hardwareMap;
    }

    @Override
    public void init() {
        hoist_dc = hardwareMap.dcMotor.get("hoist_dc");
        hoist_dc.setDirection(DcMotorSimple.Direction.REVERSE);
        front_elevator_dc = hardwareMap.dcMotor.get("front_elevator_dc");
        rear_elevator_dc = hardwareMap.dcMotor.get("rear_elevator_dc");
        harvester_dc = hardwareMap.dcMotor.get("harvester_dc");
        left_wheel_dc = hardwareMap.dcMotor.get("left_wheel_dc");
        right_wheel_dc = hardwareMap.dcMotor.get("right_wheel_dc");
        right_wheel_dc.setDirection(DcMotorSimple.Direction.REVERSE);
        blue_container_dc = hardwareMap.dcMotor.get("blue_container_dc");

        color_sensor = (LynxI2cColorRangeSensor) hardwareMap.colorSensor.get("color_sensor");

        sorter_servo = hardwareMap.servo.get("sort_servo");
        red_gate = new BallGate(hardwareMap.servo.get("red_gate"), 0, 1);
        blue_gate = new BallGate(hardwareMap.servo.get("blue_gate"), 0, 0.5);
        ball_sorter = new Sorter(sorter_servo, color_sensor, Constants.SORTER_RED_POS, Constants.SORTER_BLUE_POS, Constants.SORTER_NULL_POS);

        hoist = new Hoist(hoist_dc, Constants.HOIST_MAX_POS, 0, 1d);
    }

    @Override
    public void start() {
//        ball_sorter.start(20);
    }

    @Override
    public void stop() {
//        ball_sorter.stop();
    }

    @Override
    public void update() {
        opmode.telemetry.addData("Color", "R%d; G%d; B%d", color_sensor.red(), color_sensor.green(), color_sensor.blue());
    }
}
