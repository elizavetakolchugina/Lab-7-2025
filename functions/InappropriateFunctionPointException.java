package functions;

public class InappropriateFunctionPointException extends Exception{
    
    public InappropriateFunctionPointException(){
        super("Некорректная операция над точкой");
    }
    public InappropriateFunctionPointException(String message) {
        super(message);
    }
}

