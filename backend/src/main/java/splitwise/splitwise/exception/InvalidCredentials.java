package splitwise.splitwise.exception;

public class InvalidCredentials extends RuntimeException{
    public InvalidCredentials(String message){
        super(message);
    }
}
