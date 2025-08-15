package splitwise.splitwise.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElement(NoSuchElementException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("error: " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex){
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error: " + ex.getMessage());
    }

    @ExceptionHandler(InvalidSplitType.class)
    public ResponseEntity<String> handleInvalidSplitType(InvalidSplitType ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(ParticipantCountMismatch.class)
    public ResponseEntity<String> handlePartipantCountMismatch(ParticipantCountMismatch ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(ExactAmountSum.class)
    public ResponseEntity<String> handleExactAmountSum(ExactAmountSum ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(PercentageSumNot100.class)
    public ResponseEntity<String> handlePercentageSumNot100(PercentageSumNot100 ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyRegistered.class)
    public ResponseEntity<String> handleUserAlreadyRegistered(UserAlreadyRegistered ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(UserNotFound.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFound ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidCredentials.class)
    public ResponseEntity<String> handleInvalidCredentials(InvalidCredentials ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(UserWithPendingDues.class)
    public ResponseEntity<String> handleUserWithPendingDues(UserWithPendingDues ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(GroupNotFound.class)
    public ResponseEntity<String> handleGroupNotFound(GroupNotFound ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyExists.class)
    public ResponseEntity<String> handleGUserAlreadyExists(UserAlreadyExists ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(UserNotInGroup.class)
    public ResponseEntity<String> handleUserNotInGroup(UserNotInGroup ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(PayerAndPayeeSame.class)
    public ResponseEntity<String> handlePayerAndPayeeSame(PayerAndPayeeSame ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(NoDuesExist.class)
    public ResponseEntity<String> handleUNoDuesExist(NoDuesExist ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(ExpiredTokenException.class)
    public ResponseEntity<String> handleExpiredTokenException(ExpiredTokenException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<String> handleInvalidTokenException(InvalidTokenException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(NoGroupFoundException.class)
    public ResponseEntity<String> handleNoGroupFoundException(NoGroupFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(SettleAmountMoreThanDue.class)
    public ResponseEntity<String> handleSettleAmountMoreThanDue(SettleAmountMoreThanDue ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<String> handleEmailNotFoundException(EmailNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(ExpenseNotFound.class)
    public ResponseEntity<String> handleExpenseNotFound(ExpenseNotFound ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(GroupHasPendingDuesException.class)
    public ResponseEntity<String> handleGroupHasPendingDuesException (GroupHasPendingDuesException  ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(SettlementNotFound.class)
    public ResponseEntity<String> handleSettlementNotFound(SettlementNotFound ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }


}
