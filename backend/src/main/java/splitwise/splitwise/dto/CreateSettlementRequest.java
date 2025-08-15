package splitwise.splitwise.dto;


import lombok.Data;

@Data
public class CreateSettlementRequest {
    private Long paidby;
    private Long paidto;
    private Double amount;
}
