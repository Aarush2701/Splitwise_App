package splitwise.splitwise.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import splitwise.splitwise.dto.UserUpdateRequest;
import splitwise.splitwise.exception.EmailNotFoundException;
import splitwise.splitwise.exception.UserNotFound;
import splitwise.splitwise.exception.UserWithPendingDues;
import splitwise.splitwise.model.GroupMember;
import splitwise.splitwise.model.GroupMemberId;
import splitwise.splitwise.model.User;
import splitwise.splitwise.repository.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class UserServiceTest {

   @InjectMocks
   // @Autowired
    private UserService userService;
    // Mockbean
    @Mock
    private UserRepository userRepository;
    @Mock
    private GroupMemberRepository groupMemberRepository;
    @Mock
    private ExpenseService expenseService;
    @Mock
    private ExpenseSplitRepository expenseSplitRepository;
    @Mock
    private ExpenseRepository expenseRepository;
    @Mock
    private SettlementRepository settlementRepository;
    @Mock
    private AuthService authService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void init(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateUser_Success(){
        User existing = new User(1L,"Aarush", "aarush@gmail.com","9584321711","1");

        UserUpdateRequest updated = new UserUpdateRequest();
        updated.setEmail("aarush@gmail.com");
        updated.setUsername("Aarushnew");
        updated.setPhone("9584321711");
        updated.setPassword("1");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenReturn(existing);
        when(passwordEncoder.encode("passworrd")).thenReturn("1");
        User result = userService.updateUser(1l,updated);

        assertEquals("Aarushnew",result.getUsername());

    }

    @Test
    void testUpdateUser_Fail(){
        when(userRepository.findById(55L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class,()-> userService.updateUser(55L,new UserUpdateRequest()));
    }

    @Test
    void testGetUserById_Success(){
        Long userId = 1L;
        User existing = new User(userId,"Aarushnew", "aarush@gmail.com","9584321711","1");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existing));

        Optional <User> result = userService.getUserById(userId);
        assertTrue(result.isPresent());
        assertEquals("Aarushnew",result.get().getUsername());
    }

    @Test
    void testGetUserById_Fail(){
        when(userRepository.findById(66L)).thenReturn(Optional.empty());

        assertThrows(UserNotFound.class,() -> userService.getUserById(66L));

    }

    @Test
    void testDeleteUser_NoDues_Success(){
        Long userId = 1L;
        User user = new User(userId,"Aarushnew", "aarush@gmail.com","9584321711","1");

        GroupMemberId groupMemberId = new GroupMemberId(100L, userId);
        GroupMember groupMember = new GroupMember();
        groupMember.setId(groupMemberId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(groupMemberRepository.findById_Userid(userId)).thenReturn(List.of(groupMember));
        when(expenseSplitRepository.findByExpenseid(1L)).thenReturn(List.of());
        when(expenseRepository.findByGroupid_Groupid(100L)).thenReturn(List.of());
        when(settlementRepository.findByGroupid_Groupid(100L)).thenReturn(List.of());
        when(expenseService.getUserBalance(100L,userId)).thenReturn(List.of());

        userService.deleteUser(userId);
        verify(userRepository).deleteById(userId);
    }

    @Test
    void testDeleteUser_Dues_Fail(){
        Long userId = 1L , groupId = 100L;
        User user = new User(userId,"Aarushnew", "aarush@gmail.com","9584321711","1");

        GroupMemberId groupMemberId = new GroupMemberId(groupId, userId);
        GroupMember groupMember = new GroupMember();
        groupMember.setId(groupMemberId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(groupMemberRepository.findById_Userid(userId)).thenReturn(List.of(groupMember));
        when(expenseService.getUserBalance(groupId,userId)).thenReturn(List.of("Aarush owes Parth ₹50.0"));

        Exception ex = assertThrows(UserWithPendingDues.class, ()-> userService.deleteUser(userId));
        assertTrue(ex.getMessage().contains("pending dues") || ex.getMessage().toLowerCase().contains("cannot delete"));
        verify(userRepository, never()).deleteById(userId);
    }

    @Test
    void testDeleteUser_UserNotFound_Fail() {

        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(UserNotFound.class,() -> userService.deleteUser(2L));

    }

    @Test
    void testResolveUserIds_AllEmailsFound(){
        List<String> emails = List.of("aarush@gmail.com","aarushgoyal@gmail.com");

        User user1 = new User();
        user1.setUserid(1L);
        user1.setEmail("aarush@gmail.com");

        User user2 = new User();
        user2.setUserid(2L);
        user2.setEmail("aarushgoyal@gmail.com");

        when(userRepository.findByEmailIn(emails)).thenReturn(List.of(user1,user2));

        List<Long> userIds = userService.resolveUserIds(emails);

        assertEquals(List.of(1L,2L),userIds);
        assertEquals(2,userIds.size());
    }



    @Test
    void testResolveUserIds_EmailsNotFound(){
        List<String> emails = List.of("aarush@gmail.com","aarushgoyal@gmail.com");

        User user1 = new User();
        user1.setUserid(1L);
        user1.setEmail("aarush@gmail.com");


        when(userRepository.findByEmailIn(emails)).thenReturn(List.of(user1));

        Exception ex = assertThrows(EmailNotFoundException.class,()->userService.resolveUserIds(emails));

       assertTrue(ex.getMessage().contains("aarushgoyal@gmail.com"));

    }


}





