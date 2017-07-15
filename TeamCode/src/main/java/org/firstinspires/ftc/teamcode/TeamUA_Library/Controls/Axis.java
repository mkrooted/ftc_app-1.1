package org.firstinspires.ftc.teamcode.TeamUA_Library.Controls;

/**
 * Created by mkrooted on 30.05.2017.
 */

public class Axis {
    // Axes
    public static final int LEFT_TRIGGER = 13;
    public static final int RIGHT_TRIGGER = 14;
    public static final int LEFT_STICK_X = 15;
    public static final int LEFT_STICK_Y = 16;
    public static final int RIGHT_STICK_X = 17;
    public static final int RIGHT_STICK_Y = 18;

    public static boolean is_valid(int axis) {
        return axis > 12 && axis < 19;
    }
}
