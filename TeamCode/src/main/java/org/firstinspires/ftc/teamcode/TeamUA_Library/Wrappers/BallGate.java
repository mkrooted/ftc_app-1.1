package org.firstinspires.ftc.teamcode.TeamUA_Library.Wrappers;

import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by mkrooted on 15.06.2017.
 */

public class BallGate {
    private double closed_pos;
    private double open_pos;
    private Servo servo;

    private boolean is_open;

    public BallGate(Servo servo, double closed_position, double open_position) {
        this.servo = servo;
        closed_pos = closed_position;
        open_pos = open_position;
        servo.setPosition(closed_position);
    }

    public void open() {
        if (!is_open) {
            servo.setPosition(open_pos);
            is_open = true;
        }
    }
    public void close() {
        if (is_open) {
            servo.setPosition(closed_pos);
            is_open = false;
        }
    }

    public boolean isOpen() {
        return is_open;
    }
}
