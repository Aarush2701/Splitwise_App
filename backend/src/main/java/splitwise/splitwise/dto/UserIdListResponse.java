package splitwise.splitwise.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserIdListResponse {
    private List<Long> userIds;
}
