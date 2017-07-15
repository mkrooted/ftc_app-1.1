package org.firstinspires.ftc.teamcode.TeamUA_Library.Wrappers;

import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by mkrooted on 19.06.2017.
 */

public class Hoist {
    private DcMotor motor;
    private int top_pos = 16400;
    private int bottom_pos = 0;
    private double power = 1.0;

    private boolean is_moving = false;
    private boolean direction = true;  // true - up; false - down;

    public Hoist(DcMotor motor) {
        this.motor = motor;
    }

    public Hoist(DcMotor motor, int top_pos, int bottom_pos, double power) {
        this.motor = motor;
        this.top_pos = top_pos;
        this.bottom_pos = bottom_pos;
        this.power = power;
    }

    public boolean ascend() {
        if (!is_moving && canAscend()) {
            is_moving = true;
            direction = true;
            motor.setPower(1);
            return true;
        }
        return false;
    }
    public boolean descend() {
        if (!is_moving && canDescend()) {
            is_moving = true;
            direction = false;
            motor.setPower(-1);
            return true;
        }
        return false;
    }

    public void stop() {
        if (is_moving) {
            motor.setPower(0d);
            is_moving = false;
        }
    }

    public void update() {
        if (is_moving && ((direction && !canAscend()) || (!direction && !canDescend()))) {
            motor.setPower(0d);
        }
    }

    public boolean canAscend() {
        return  ( motor.getCurrentPosition() < top_pos);
    }
    public boolean canDescend() {
        return  ( motor.getCurrentPosition() > bottom_pos);
    }
}
