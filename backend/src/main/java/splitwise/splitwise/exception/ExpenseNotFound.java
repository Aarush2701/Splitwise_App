package splitwise.splitwise.exception;

public class ExpenseNotFound extends RuntimeException{
    public ExpenseNotFound(String message){
        super(message);
    }
}
