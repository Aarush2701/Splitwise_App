package splitwise.splitwise.dto;

import lombok.Data;

@Data
public class SignupRequest {

    private String username;

    private String email;

    private String phone;

    private String password;
}
