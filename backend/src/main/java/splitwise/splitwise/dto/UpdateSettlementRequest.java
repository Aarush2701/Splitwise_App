package splitwise.splitwise.dto;

import lombok.Data;

@Data
public class UpdateSettlementRequest {
    private Long paidby;
    private Long paidto;
    private Double amount;
}