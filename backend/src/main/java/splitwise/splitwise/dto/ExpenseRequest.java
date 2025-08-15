package splitwise.splitwise.dto;


import lombok.Data;

@Data
public class ExpenseRequest {
    private Long userid;
    private Double amount;
    private String description;
}
