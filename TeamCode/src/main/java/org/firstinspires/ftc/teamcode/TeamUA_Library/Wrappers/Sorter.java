package org.firstinspires.ftc.teamcode.TeamUA_Library.Wrappers;

import com.qualcomm.hardware.lynx.LynxI2cColorRangeSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

import java.util.Timer;
import java.util.TimerTask;

public class Sorter {
    private Servo servo;
    private LynxI2cColorRangeSensor color_sensor;
    private double red_pos;
    private double blue_pos;
    private double null_pos;

    private Timer update_loop;
    private Timer servo_loop;
    private boolean ball_in_progress;
    private int ball_type;

    public static final int GENERAL_DELAY = 10; // in ms
    public static final int ROLL_BACK_DELAY = 400; // in ms

    public static final double DEFAULT_RED_POS = 0;
    public static final double DEFAULT_BLUE_POS = 1;
    public static final double DEFAULT_NULL_POS = 0.5;

    public Sorter(Servo servo, LynxI2cColorRangeSensor sensor) {
        this(servo, sensor, 0.09, 0.23, 0.16);
    }

    public Sorter(Servo servo, LynxI2cColorRangeSensor sensor, double red_pos, double blue_pos, double null_pos) {
        this.servo = servo;
        this.color_sensor = sensor;
        this.red_pos = red_pos;
        this.blue_pos = blue_pos;
        this.null_pos = null_pos;
        ball_in_progress = false;
        ball_type = Ball.NONE;
        servo_loop = new Timer();
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
        int r = color_sensor.red(), g = color_sensor.green(), b = color_sensor.blue();
        if (color_sensor.getDistance(DistanceUnit.MM) <= 200) {
            if (r > 1.8 * b) {
                setBall(Ball.RED);
            } else if (b > 1.5 * r) {
                setBall(Ball.BLUE);
            }
        }
    }

    public void setBall(final int type) {
        if (!ball_in_progress) {
            ball_type = type;
            ball_in_progress = true;
            servo_loop.schedule(new TimerTask() {
                @Override
                public void run() {
                    switch (type) {
                        case Ball.BLUE:
                            servo.setPosition(blue_pos);
                            break;
                        case Ball.RED:
                            servo.setPosition(red_pos);
                            break;
                        default:
                            servo.setPosition(null_pos);
                    }
                }
            }, GENERAL_DELAY);
            servo_loop.schedule(new TimerTask() {
                @Override
                public void run() {
                    servo.setPosition(null_pos);
                    ball_in_progress = false;
                    ball_type = Ball.NONE;
                }
            }, GENERAL_DELAY + ROLL_BACK_DELAY);
        }
    }

    public int getCurrentBall() {
        return ball_type;
    }

    public static class Ball {
        public static final int NONE = 0;
        public static final int RED = 1;
        public static final int BLUE = 2;
    }
}
