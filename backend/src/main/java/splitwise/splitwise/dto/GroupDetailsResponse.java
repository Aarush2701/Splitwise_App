package splitwise.splitwise.dto;


import lombok.Data;

import java.util.List;

@Data
public class GroupDetailsResponse {
    private Long groupId;
    private String groupname;
    private List<GroupMemberDTO> members;
}
