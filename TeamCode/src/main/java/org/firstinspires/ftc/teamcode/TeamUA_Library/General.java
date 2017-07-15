package org.firstinspires.ftc.teamcode.TeamUA_Library;

import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by mkrooted on 30.05.2017.
 */

public class General {
    public static void waitForTick(ElapsedTime period, long periodMs) throws java.lang.InterruptedException {
        long remaining = periodMs - (long) period.milliseconds();

        if (remaining > 0) {
            Thread.sleep(remaining);
        }
        period.reset();
    }
}
