package exceptions;

public class InsufficientFuelException extends Exception {
    public InsufficientFuelException(){
        super();
    }
    public InsufficientFuelException(String message)
    {
        super(message);
    }
}
