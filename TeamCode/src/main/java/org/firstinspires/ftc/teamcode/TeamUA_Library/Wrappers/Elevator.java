package org.firstinspires.ftc.teamcode.TeamUA_Library.Wrappers;

import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * Created by mkrooted on 20.06.2017.
 */

public class Elevator {
    private DcMotor rear_dc;
    private DcMotor front_dc;

    private double power;

    private boolean direction = true;
    private boolean is_running = false;

    public Elevator(DcMotor rear_dc, DcMotor front_dc) {
        this(rear_dc, front_dc, 1d);
    }

    public Elevator(DcMotor rear_dc, DcMotor front_dc, double power) {
        this.rear_dc = rear_dc;
        this.front_dc = front_dc;
        this.power = power;
        if (rear_dc.getPower() > 0.2 && front_dc.getPower() > 0.2) is_running = true;
    }

    public void start() {
        if (!is_running) {
            is_running = true;
            rear_dc.setPower(power);
            front_dc.setPower(power);
        }
    }

    public void stop() {
        if (is_running) {
            is_running = false;
            rear_dc.setPower(0d);
            front_dc.setPower(0d);
        }
    }

    public void reverse() {
        power = -power;
        if (is_running) {
            stop();
            start();
        }
    }

    public boolean isActive() {
        return is_running;
    }

    public DcMotor getRear_dc() {
        return rear_dc;
    }

    public void setRear_dc(DcMotor rear_dc) {
        this.rear_dc = rear_dc;
    }

    public DcMotor getFront_dc() {
        return front_dc;
    }

    public void setFront_dc(DcMotor front_dc) {
        this.front_dc = front_dc;
    }
}
