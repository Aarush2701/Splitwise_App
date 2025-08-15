package splitwise.splitwise.exception;

public class UserWithPendingDues extends RuntimeException{
    public UserWithPendingDues(String message){
        super(message);
    }
}
