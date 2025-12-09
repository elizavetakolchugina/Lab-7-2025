package functions;

import functions.meta.*;

public final class Functions {
    private Functions() {
        throw new Error("Объекты этого класса нельзя создать");
    }

    public static Function shift(Function f, double shiftX, double shiftY) {
        return new Shift(f, shiftX, shiftY);
    }

    public static Function scale(Function f, double scaleX, double scaleY) {
        return new Scale(f, scaleX, scaleY);
    }

    public static Function power(Function f, double power) {
        return new Power(f, power);
    }

    public static Function sum(Function f_1, Function f_2) {
        return new Sum(f_1, f_2);
    }

    public static Function mult(Function f_1, Function f_2) {
        return new Mult(f_1, f_2);
    }

    public static Function composition(Function f_1, Function f_2) {
        return new Composition(f_1, f_2);
    }

    public static double integrate(Function f, double leftX, double rightX, double step) {
        if (leftX < f.getLeftDomainBorder() || rightX > f.getRightDomainBorder()) {
            throw new IllegalArgumentException("Интервал интегрирования за границами области определения");
        }
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быь меньше правой");
        }
        if (step <= 0) {
            throw new IllegalArgumentException("Шаг не положителен");
        }

        double integral = 0.0;
        double x = leftX;
        double yPrevious = f.getFunctionValue(x);

        while (x < rightX) {
            double xNext = Math.min(x + step, rightX);
            double yNext = f.getFunctionValue(xNext);

            integral += (yPrevious + yNext) * (xNext - x) / 2;
            x = xNext;
            yPrevious = yNext;
        }
        return integral;

    }
}

