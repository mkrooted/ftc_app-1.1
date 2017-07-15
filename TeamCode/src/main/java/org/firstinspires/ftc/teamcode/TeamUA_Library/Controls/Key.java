package org.firstinspires.ftc.teamcode.TeamUA_Library.Controls;

/**
 * Created by mkrooted on 30.05.2017.
 */

public class Key {
    // Buttons
    public static final int X = 1;
    public static final int Y = 2;
    public static final int A = 3;
    public static final int B = 4;
    public static final int LEFT_BUTTON = 5;
    public static final int RIGHT_BUTTON = 6;
    public static final int LEFT_STICK_BTN = 7;
    public static final int RIGHT_STICK_BTN = 8;

    //D-PAD
    public static final int DPAD_UP = 9;
    public static final int DPAD_DOWN = 10;
    public static final int DPAD_LEFT = 11;
    public static final int DPAD_RIGHT = 12;

    public static boolean is_valid(int key) {
        return key > 0 && key < 13;
    }
    public static boolean is_dpad(int key) {
        return key > 8 && key < 13;
    }
}
