package org.firstinspires.ftc.teamcode.Release;

import org.firstinspires.ftc.teamcode.TeamUA_Library.Controls.AxisMiddlewares.AxisMiddleware;

/**
 * Created by mkrooted on 20.06.2017.
 */

public class PowMiddleware extends AxisMiddleware {
    private double a;
    private double b;
    private double c;

    PowMiddleware(AxisMiddleware next_middleware) {
        super(next_middleware);
        a = 1;
        b = 0;
        c = 0;
    }

    PowMiddleware() {
        this(null);
    }

    @Override
    protected double f(double input) {
        return a * Math.pow(input, 1.8) + b * input + c;
    }
}
