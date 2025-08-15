package splitwise.splitwise.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import splitwise.splitwise.dto.JwtResponse;
import splitwise.splitwise.dto.LoginRequest;
import splitwise.splitwise.dto.SignupRequest;
import splitwise.splitwise.exception.InvalidCredentials;
import splitwise.splitwise.exception.UserAlreadyRegistered;
import splitwise.splitwise.exception.UserNotFound;
import splitwise.splitwise.model.User;
import splitwise.splitwise.repository.UserRepository;
import splitwise.splitwise.utility.JwtUtil;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public JwtResponse signup(SignupRequest request) {
        if(userRepository.existsByEmail(request.getEmail())){
            throw new UserAlreadyRegistered("User is already registered with this email");
        }

        if(userRepository.existsByPhone(request.getPhone())){
            throw new UserAlreadyRegistered("User is already registered with this number");
        }

        if(userRepository.existsByUsername(request.getUsername())){
            throw new UserAlreadyRegistered("User is already registered with this username");
        }


        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

          User savedUser = userRepository.save(user);

        String token = jwtUtil.generateToken(savedUser.getEmail(), savedUser.getUserid());
        return new JwtResponse(token);
    }

    public JwtResponse login(LoginRequest request){
        User user = userRepository.findByEmailOrPhone(request.getIdentifier(), request.getIdentifier())
                .orElseThrow(()->new UserNotFound("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())){
            throw new InvalidCredentials("Invaild credentials");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getUserid());
        return new JwtResponse(token);
    }

}
