package splitwise.splitwise.exception;

public class UserAlreadyExists extends RuntimeException{
    public UserAlreadyExists(String message){
        super(message);
    }
}
