package splitwise.splitwise.exception;

public class SettlementNotFound extends RuntimeException{
    public SettlementNotFound (String message){
        super(message);
    }
}
