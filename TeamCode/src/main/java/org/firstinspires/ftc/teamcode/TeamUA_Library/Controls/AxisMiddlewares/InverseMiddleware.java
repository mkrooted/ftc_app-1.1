package org.firstinspires.ftc.teamcode.TeamUA_Library.Controls.AxisMiddlewares;

        public class InverseMiddleware extends AxisMiddleware {
            private boolean is_inverse;

            public InverseMiddleware(boolean is_normal_negated, AxisMiddleware next_middleware) {
                super(next_middleware);
                this.is_inverse = is_normal_negated;
    }

    public void set_state(boolean inversed) {
        is_inverse = inversed;
    }

    @Override
    protected double f(double input) {
        return is_inverse ? -input : input;
    }
}
