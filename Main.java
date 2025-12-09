import functions.*;
import functions.basic.Cos;
import functions.basic.Sin;

public class Main {
    public static void main(String[] args) {
        System.out.println("__testing iterator ArrayTabulatedFunction__");
        FunctionPoint[] points1 = {
            new FunctionPoint(1.0, 1.0),
            new FunctionPoint(2.0, 4.0),
            new FunctionPoint(3.0, 9.0)
        };
        TabulatedFunction f_1 = new ArrayTabulatedFunction(points1);
        for (FunctionPoint p : f_1) {
            System.out.println(p);
        }

        System.out.println("__testing iterator LinkedListTabulatedFunction__");
        TabulatedFunction f_2 = new LinkedListTabulatedFunction(points1);
        for (FunctionPoint p : f_2) {
            System.out.println(p);
        }

        System.out.println("__testing factory__");
        Function f = new Cos();
        TabulatedFunction tf;

        tf = TabulatedFunctions.tabulate(f, 0, Math.PI, 11);
        System.out.println("Array factory: " + tf.getClass());

        TabulatedFunctions.setTabulatedFunctionFactory(new LinkedListTabulatedFunction.LinkedListTabulatedFunctionFactory());
        tf = TabulatedFunctions.tabulate(f, 0, Math.PI, 11);
        System.out.println("LinkedList factory: " + tf.getClass());

        TabulatedFunctions.setTabulatedFunctionFactory(new ArrayTabulatedFunction.ArrayTabulatedFunctionFactory());
        tf = TabulatedFunctions.tabulate(f, 0, Math.PI, 11);
        System.out.println("Array factory: " + tf.getClass());

        System.out.println("\n__testing reflection__");

        f = TabulatedFunctions.createTabulatedFunction(ArrayTabulatedFunction.class, 0, 10, 3);
        System.out.println("ArrayTabulatedFunction (double, double, int): " + f.getClass());
        System.out.println(f);

        f = TabulatedFunctions.createTabulatedFunction(
        ArrayTabulatedFunction.class, 0, 10, new double[] {0, 10});
        System.out.println("\nArrayTabulatedFunction (double, double, double[]): " + f.getClass());
        System.out.println(f);

        f = TabulatedFunctions.createTabulatedFunction(
        LinkedListTabulatedFunction.class, 
        new FunctionPoint[] {
            new FunctionPoint(0, 0),
            new FunctionPoint(10, 10)
        }
        );
        System.out.println("\nLinkedListTabulatedFunction (FunctionPoint[]): " + f.getClass());
        System.out.println(f);

        f = TabulatedFunctions.tabulate(LinkedListTabulatedFunction.class, new Sin(), 0, Math.PI, 11);
        System.out.println("\ntabulate with reflection (LinkedListTabulatedFunction, Sin): " + f.getClass());
        System.out.println(f);
    }
}
