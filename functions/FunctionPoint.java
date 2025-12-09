package functions;

import java.io.Serializable;

public class FunctionPoint implements Serializable, Cloneable{

    private static final long serialVersionUID = 1L;
    private double x;
    private double y;
    private static final double EPSILON = 1e-9;

    public double getX(){
        return x;
    }
    public void setX(double x){
        this.x = x;
    }

    public double getY(){
        return y;
    }
    public void setY(double y){
        this.y = y;
    }


    public FunctionPoint(double x, double y){
        this.x = x;
        this.y = y; 
    }

    public FunctionPoint(FunctionPoint point){
        this.x = point.x;
        this.y = point.y;
    }

    public FunctionPoint(){
        this.x = 0;
        this.y = 0;
    }

    @Override
    public String toString() {
        return "(" + this.x + ";" + this.y + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FunctionPoint oPoint = (FunctionPoint) o;
        return doubleEquals(this.x, oPoint.x) && doubleEquals(this.y, oPoint.y);

    }

    @Override
    public int hashCode() {
        return Double.hashCode(x) ^ Double.hashCode(y); 
    }

    @Override
    public Object clone() {
        return new FunctionPoint(this);
    }

    private boolean doubleEquals(double a, double b) {
        return Math.abs(a - b) < EPSILON;
    }
}