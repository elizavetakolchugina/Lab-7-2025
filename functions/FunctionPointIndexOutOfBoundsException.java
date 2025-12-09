package functions;

public class FunctionPointIndexOutOfBoundsException extends IndexOutOfBoundsException{

    public FunctionPointIndexOutOfBoundsException() {
        super("Выход за границы набора точек функции");
    }

    public FunctionPointIndexOutOfBoundsException(String message) {
        super(message);
    }
}


