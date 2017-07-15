package org.firstinspires.ftc.teamcode.Release;

import org.firstinspires.ftc.teamcode.TeamUA_Library.Controls.AxisMiddlewares.AxisMiddleware;

/**
 * Created by mkrooted on 20.06.2017.
 */

public class LogMiddleware extends AxisMiddleware {
    @Override
    protected double f(double input) {
        return Math.sqrt(input);
    }
}
