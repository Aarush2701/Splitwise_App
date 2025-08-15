package splitwise.splitwise.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GroupMemberDTO {
    private Long userId;
    private String username;
}
