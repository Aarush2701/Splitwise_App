package splitwise.splitwise.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExpenseSplitResponse {
    private String username;
    private Double amount;
}