package splitwise.splitwise.exception;


public class SettleAmountMoreThanDue extends RuntimeException {
    public SettleAmountMoreThanDue(String message){
        super(message);
    }
}