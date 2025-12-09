package functions.basic;

import functions.Function;

public class Log implements Function {
    private double base;

    public Log(double base) {
        if(base <= 0 || base == 1) {
            throw new IllegalArgumentException("Основание должно быть больше 0 и не равно 1");
        }
        this.base = base;
    }

    @Override
    public double getLeftDomainBorder() {
        return 0.0;
    }

    @Override
    public double getRightDomainBorder() {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public double getFunctionValue(double x){
        if (x <= 0){
            return Double.NaN;
        }
        return Math.log(x) / Math.log(base);        // Math.log - это натуральный логарифм 
    }
  
}