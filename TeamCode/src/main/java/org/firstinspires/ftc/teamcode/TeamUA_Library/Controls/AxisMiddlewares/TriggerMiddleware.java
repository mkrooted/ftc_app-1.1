package org.firstinspires.ftc.teamcode.TeamUA_Library.Controls.AxisMiddlewares;

/**
 * Created by mkrooted on 15.06.2017.
 */


public class TriggerMiddleware extends AxisMiddleware {
    private double trigger;
    private double out;

    public TriggerMiddleware(boolean inverted) {
        this(0.5d, inverted);
    }

    public TriggerMiddleware(boolean inverted, AxisMiddleware next_middleware) {
        this(0.5d, inverted, next_middleware);
    }

    public TriggerMiddleware(double activation_value, boolean inverted) {
        this(activation_value, inverted, null);
    }

    public TriggerMiddleware(double activation_value, boolean inverted, AxisMiddleware next_middleware) {
        super(next_middleware);
        trigger = activation_value;
        out = inverted ? -1d : 1d;
    }

    @Override
    protected double f(double input) {
        return (input > trigger) ? out : 0;
    }
}