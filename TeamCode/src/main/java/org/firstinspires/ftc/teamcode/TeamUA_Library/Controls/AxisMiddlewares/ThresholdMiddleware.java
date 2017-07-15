package org.firstinspires.ftc.teamcode.TeamUA_Library.Controls.AxisMiddlewares;

/**
 * Created by mkrooted on 15.06.2017.
 */


public class ThresholdMiddleware extends AxisMiddleware {
    private double l_threshold;
    private double u_threshold;

    ThresholdMiddleware(double lower_threshold, double upper_threshold, AxisMiddleware next_middleware) {
        super(next_middleware);
        l_threshold = lower_threshold;
        u_threshold = upper_threshold;
    }

    public double f(double input) {
        return (input > l_threshold) ? (input < u_threshold ? input : u_threshold) : l_threshold;
    }
}