package splitwise.splitwise.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import splitwise.splitwise.dto.JwtResponse;
import splitwise.splitwise.dto.LoginRequest;
import splitwise.splitwise.dto.SignupRequest;
import splitwise.splitwise.exception.InvalidCredentials;
import splitwise.splitwise.exception.UserAlreadyExists;
import splitwise.splitwise.exception.UserAlreadyRegistered;
import splitwise.splitwise.exception.UserNotFound;
import splitwise.splitwise.model.User;
import splitwise.splitwise.repository.UserRepository;
import splitwise.splitwise.utility.JwtUtil;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @BeforeEach
    void init(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSignup_Success(){
        Long userid = 1L;
        SignupRequest request = new SignupRequest();
        request.setUsername("Aarush");
        request.setEmail("aar@gmail.com");
        request.setPhone("1234567890");
        request.setPassword("password");

        when(userRepository.findByEmailOrPhone("aar@gmail.com","1234567890")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        User savedUser = new User(userid,"Aarush","aar@gmail.com","1234567890","encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        String token = "mockedtoken";

        when(jwtUtil.generateToken("aar@gmail.com",savedUser.getUserid())).thenReturn("mockedtoken");

        JwtResponse result = authService.signup(request);

        assertEquals(token, result.getToken());
        verify(userRepository).save(any(User.class));

    }

    @Test
    void testSignup_UserAlreadyExists_ByEmail() {
        Long userid = 1L;
        SignupRequest request = new SignupRequest();
        request.setUsername("Aarush");
        request.setEmail("aar@gmail.com");
        request.setPhone("1234567890");
        request.setPassword("password");

        when(userRepository.existsByEmail("aar@gmail.com")).thenReturn(true);

        Exception ex = assertThrows(UserAlreadyRegistered.class, () -> authService.signup(request));

        assertEquals("User is already registered with this email", ex.getMessage());
    }


    @Test
    void testSignup_UserAlreadyExists_ByPhone() {
        Long userid = 1L;
        SignupRequest request = new SignupRequest();
        request.setUsername("Aarush");
        request.setEmail("aar@gmail.com");
        request.setPhone("1234567890");
        request.setPassword("password");

        when(userRepository.existsByPhone("1234567890")).thenReturn(true);

        Exception ex = assertThrows(UserAlreadyRegistered.class, () -> authService.signup(request));

        assertEquals("User is already registered with this number", ex.getMessage());
    }


    @Test
    void testSignup_UserAlreadyExists_ByUsername() {
        Long userid = 1L;
        SignupRequest request = new SignupRequest();
        request.setUsername("Aarush");
        request.setEmail("aar@gmail.com");
        request.setPhone("1234567890");
        request.setPassword("password");

        when(userRepository.existsByUsername("Aarush")).thenReturn(true);

        Exception ex = assertThrows(UserAlreadyRegistered.class, () -> authService.signup(request));

        assertEquals("User is already registered with this username", ex.getMessage());
    }


    @Test
    void testLogin_ByEmail(){
        Long userid = 1L;
        LoginRequest request = new LoginRequest();
        request.setIdentifier("aar@gmail.com");
        request.setPassword("password");

        User user = new User(userid,"Aarush","aar@gmail.com","1234567890","encodedPassword");

        when(userRepository.findByEmailOrPhone("aar@gmail.com","aar@gmail.com")).thenReturn(Optional.of(user));

        when(passwordEncoder.matches("password","encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("aar@gmail.com",userid)).thenReturn("mockedtoken");

        JwtResponse result = authService.login(request);

        assertEquals("mockedtoken",result.getToken());
    }

    @Test
    void testLogin_ByPhone(){
        Long userid = 1L;
        LoginRequest request = new LoginRequest();
        request.setIdentifier("1234567890");
        request.setPassword("password");

        User user = new User(userid,"Aarush","aar@gmail.com","1234567890","encodedPassword");

        when(userRepository.findByEmailOrPhone("1234567890","1234567890")).thenReturn(Optional.of(user));

        when(passwordEncoder.matches("password","encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("aar@gmail.com",userid)).thenReturn("mockedtoken");

        JwtResponse result = authService.login(request);

        assertEquals("mockedtoken",result.getToken());
    }

    @Test
    void testLogin_UserNotFound(){
        Long userid = 1L;
        LoginRequest request = new LoginRequest();
        request.setIdentifier("1234567890");
        request.setPassword("password");

        User user = new User(userid,"Aarush","aar@gmail.com","1234567890","encodedPassword");

        when(userRepository.findByEmailOrPhone("1234567890","1234567890")).thenReturn(Optional.empty());

        Exception ex = assertThrows(UserNotFound.class,()->authService.login(request));

        assertEquals("User not found",ex.getMessage());
    }

    @Test
    void testLogin_InvalidCredentials() {
        Long userid = 1L;
        LoginRequest request = new LoginRequest();
        request.setIdentifier("1234567890");
        request.setPassword("password");

        User user = new User(userid, "Aarush", "aar@gmail.com", "1234567890", "encodedPassword");

        when(userRepository.findByEmailOrPhone("1234567890", "1234567890")).thenReturn(Optional.of(user));

        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(false);

        Exception ex = assertThrows(InvalidCredentials.class, () -> authService.login(request));

        assertEquals("Invaild credentials", ex.getMessage());

    }


}
