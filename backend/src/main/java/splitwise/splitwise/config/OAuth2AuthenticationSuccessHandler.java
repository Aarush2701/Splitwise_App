package splitwise.splitwise.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import splitwise.splitwise.model.User;
import splitwise.splitwise.repository.UserRepository;
import splitwise.splitwise.utility.JwtUtil;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        Map<String, Object> attributes = oauthToken.getPrincipal().getAttributes();

        String email = (String) attributes.get("email");
        String username = email.substring(0, email.indexOf("@"));

        // Register user if not exists
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUsername(username);
            newUser.setPassword(UUID.randomUUID().toString()); // set a random password
            return userRepository.save(newUser);
        });

        User savedUser = userRepository.save(user);
        // Generate JWT
        String jwt = jwtTokenUtil.generateToken(savedUser.getEmail(), savedUser.getUserid());


        String redirectUrl = "http://localhost:3000/oauth2/redirect?token=" + jwt;
        response.sendRedirect(redirectUrl);

    }
}