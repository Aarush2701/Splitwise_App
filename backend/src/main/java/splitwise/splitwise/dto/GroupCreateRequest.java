package splitwise.splitwise.dto;


import lombok.Data;

import java.util.List;

@Data
public class GroupCreateRequest {
    private String groupname;
    private List<Long> userIds;
}
