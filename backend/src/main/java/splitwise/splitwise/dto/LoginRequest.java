package splitwise.splitwise.dto;

import lombok.Data;

@Data
public class LoginRequest {

    private String identifier;
    private String password;
}
