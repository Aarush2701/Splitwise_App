package splitwise.splitwise.exception;

public class PercentageSumNot100 extends RuntimeException {
    public PercentageSumNot100(String message){
        super(message);
    }
}
