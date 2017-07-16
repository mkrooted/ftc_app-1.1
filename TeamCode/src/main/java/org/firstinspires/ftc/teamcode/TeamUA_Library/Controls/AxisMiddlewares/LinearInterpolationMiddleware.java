package org.firstinspires.ftc.teamcode.TeamUA_Library.Controls.AxisMiddlewares;

public class LinearInterpolationMiddleware extends AxisMiddleware {
    private double k, bias;

    LinearInterpolationMiddleware(double x1, double y1, double x2, double y2) {
        this(x1, y1, x2, y2, null);
    }

    public LinearInterpolationMiddleware(double x1, double y1, double x2, double y2, AxisMiddleware next_middleware) {
        super(next_middleware);
        k = (y2 - y1) / (x2 - x1);
        bias = (y1 * x2 - y2 * x1) / (x2 - x1);
    }

    public LinearInterpolationMiddleware(double x, double y) {
        this(x, y, null);
    }

    public LinearInterpolationMiddleware(double x, double y, AxisMiddleware next_middleware) {
        super(next_middleware);
        k = y / x;
        bias = 0;
    }

    @Override
    protected double f(double input) {
        return input * k + bias;
    }
}