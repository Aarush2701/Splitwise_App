package splitwise.splitwise.dto;

import lombok.Data;
import splitwise.splitwise.enums.SplitType;

import java.util.List;

@Data
public class AddExpenseRequest {
    private Double amount;
    private String description;
    private Long paidBy;
    private List<Long> participants;
    private List<Double> values;
    private SplitType splitType;

}
