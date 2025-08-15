package splitwise.splitwise.dto;

import lombok.Data;

import java.util.List;

@Data
public class EmailListRequest {
    private List<String> emails;
}
