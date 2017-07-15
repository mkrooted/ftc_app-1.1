package org.firstinspires.ftc.teamcode.TeamUA_Library.Controls.AxisMiddlewares;

/**
 * Created by mkrooted on 15.06.2017.
 */


public class ConditionalMiddleware extends AxisMiddleware {
    public static final int STATE_PROHIBIT = 0;
    public static final int STATE_PERMIT_POSITIVE = 1;
    public static final int STATE_PERMIT_NEGATIVE = 2;
    public static final int STATE_PERMIT_ALL = 3;

    private boolean up_condition;
    private boolean down_condition;

    public ConditionalMiddleware(int initial_state) {
        this(null, initial_state);
    }

    public ConditionalMiddleware(AxisMiddleware next_middleware, int initial_state) {
        super(next_middleware);
        setState(initial_state);
    }

    public void setState(int state) {
        up_condition = (state & STATE_PERMIT_POSITIVE) == 1;
        down_condition = (state & STATE_PERMIT_NEGATIVE) == 2;
    }

    public boolean getState() {
        return up_condition | down_condition;
    }

    @Override
    protected double f(double input) {
        return input > 0 ? up_condition ? input : 0 : down_condition ? input : 0;
    }
}