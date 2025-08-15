package splitwise.splitwise.exception;

public class PayerAndPayeeSame extends RuntimeException{
    public PayerAndPayeeSame(String message){
        super(message);
    }
}
