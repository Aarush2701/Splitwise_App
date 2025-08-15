package splitwise.splitwise.dto;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String username;
    private String phone;
    private String email;
    private String password;
}
