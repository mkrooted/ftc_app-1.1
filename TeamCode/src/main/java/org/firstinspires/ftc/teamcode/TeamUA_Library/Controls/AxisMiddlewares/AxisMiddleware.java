package org.firstinspires.ftc.teamcode.TeamUA_Library.Controls.AxisMiddlewares;

/**
 * Created by mkrooted on 15.06.2017.
 */


public abstract class AxisMiddleware {
    private AxisMiddleware next;

    public AxisMiddleware(AxisMiddleware next_middleware) {
        next = next_middleware;
    }

    public AxisMiddleware() {
        next = null;
    }

    public double forward(double input) {
        if (next == null) {
            return f(input);
        }
        return next.forward(input);
    }

    abstract protected double f(double input);
}