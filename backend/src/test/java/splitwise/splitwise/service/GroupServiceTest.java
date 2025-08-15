package splitwise.splitwise.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import splitwise.splitwise.dto.GroupDetailsResponse;
import splitwise.splitwise.exception.*;
import splitwise.splitwise.model.*;
import splitwise.splitwise.repository.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


public class GroupServiceTest {

    //Autowire
   @InjectMocks
    private GroupService groupService;

   //MockitoBean or MockBean
    @Mock
    private ExpenseGroupRepository groupRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GroupMemberRepository groupMemberRepository;

    @Mock
    private ExpenseService expenseService;

    @Mock
    private ExpenseSplitRepository expenseSplitRepository;

    @Mock
    private SettlementRepository settlementRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    SettlementService settlementService;


    @BeforeEach
    void init(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateGroup_Success(){
        List<Long> userIds = List.of(1L,2L);
        ExpenseGroup group = new ExpenseGroup(1L,"Trip");

        when(groupRepository.save(any(ExpenseGroup.class))).thenReturn(group);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L,"Aarush", "aarush@gmail.com","9584321711","1")));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User(2L,"Parth", "parth@gmail.com","9584320000","1")));

        ExpenseGroup result = groupService.createGroup("Trip",userIds);

        assertEquals("Trip", result.getGroupname());
        verify(groupMemberRepository, times(userIds.size())).save(any(GroupMember.class));
    }

    @Test
    void testCreateGroup_Fail() {
        List<Long> userIds = List.of(1L, 2L, 3L);
        ExpenseGroup group = new ExpenseGroup(1L, "Trip");
        when(groupRepository.save(any(ExpenseGroup.class))).thenReturn(group);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "Aarush", "aarush@gmail.com", "9584321711","1")));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User(2L, "Parth", "parth@gmail.com", "9584320000","1")));


      Exception ex =  assertThrows(UserNotFound.class, () -> groupService.createGroup("Trip", userIds));
      assertTrue(ex.getMessage().contains("User not found"));
    }

    @Test
    void testGetGroup_Success(){
        Long groupId = 1L;
        ExpenseGroup group = new ExpenseGroup(groupId,"Trip");

        GroupMemberId memberId1 = new GroupMemberId(groupId,1L);
        GroupMember member1 = new GroupMember();
        member1.setId(memberId1);
        member1.setJoiningdate(new Timestamp(System.currentTimeMillis()));

        GroupMemberId memberId2 = new GroupMemberId(groupId,2L);
        GroupMember member2 = new GroupMember();
        member2.setId(memberId2);
        member2.setJoiningdate(new Timestamp(System.currentTimeMillis()));

        List<GroupMember> members = List.of(member1,member2);

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(groupMemberRepository.findById_Groupid(groupId)).thenReturn(members);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "Aarush", "aarush@gmail.com", "9584321711","1")));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User(2L, "Parth", "parth@gmail.com", "9584320000","1")));

        GroupDetailsResponse response = groupService.getGroup(groupId);

        assertEquals("Trip",response.getGroupname());
        assertEquals(2,response.getMembers().size());
    }

    @Test
    void testGetGroup_GroupNotFound(){
        Long groupId = 1L;
        ExpenseGroup group = new ExpenseGroup(groupId,"Trip");
        GroupMemberId memberId1 = new GroupMemberId(groupId,1L);
        GroupMember member1 = new GroupMember();
        member1.setId(memberId1);
        member1.setJoiningdate(new Timestamp(System.currentTimeMillis()));

        GroupMemberId memberId2 = new GroupMemberId(groupId,2L);
        GroupMember member2 = new GroupMember();
        member2.setId(memberId2);
        member2.setJoiningdate(new Timestamp(System.currentTimeMillis()));

        List<GroupMember> members = List.of(member1,member2);

        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());
        when(groupMemberRepository.findById_Groupid(groupId)).thenReturn(members);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "Aarush", "aarush@gmail.com", "9584321711","1")));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User(2L, "Parth", "parth@gmail.com", "9584320000","1")));

        Exception ex =  assertThrows(GroupNotFound.class,()->groupService.getGroup(groupId));
        assertTrue(ex.getMessage().contains("Group not found"));
    }

    @Test
    void testGetGroup_UserNotFound(){
        Long groupId = 1L;
        ExpenseGroup group = new ExpenseGroup(groupId,"Trip");
        GroupMemberId memberId1 = new GroupMemberId(groupId,1L);
        GroupMember member1 = new GroupMember();
        member1.setId(memberId1);
        member1.setJoiningdate(new Timestamp(System.currentTimeMillis()));

        GroupMemberId memberId2 = new GroupMemberId(groupId,2L);
        GroupMember member2 = new GroupMember();
        member2.setId(memberId2);
        member2.setJoiningdate(new Timestamp(System.currentTimeMillis()));

        GroupMemberId memberId3 = new GroupMemberId(groupId,3L);
        GroupMember member3 = new GroupMember();
        member3.setId(memberId3);
        member3.setJoiningdate(new Timestamp(System.currentTimeMillis()));

        List<GroupMember> members = List.of(member1,member2,member3);

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(groupMemberRepository.findById_Groupid(groupId)).thenReturn(members);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "Aarush", "aarush@gmail.com", "9584321711","1")));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User(2L, "Parth", "parth@gmail.com", "9584320000","1")));
        when(userRepository.findById(3L)).thenReturn(Optional.empty());
        Exception ex = assertThrows(UserNotFound.class,()->groupService.getGroup(groupId));
        assertTrue(ex.getMessage().contains("User not found"));
    }

    @Test
    void testAddUserToGroup_Success(){
        Long groupId = 1l, userId = 3L;
        when(groupMemberRepository.existsById_GroupidAndId_Userid(groupId,userId)).thenReturn(false);
        when(userRepository.findById(3L)).thenReturn(Optional.of(new User(3L,"Raju","raju@gmail.com","9584321211","1")));
        ExpenseGroup group = new ExpenseGroup(groupId,"Trip");
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        groupService.addUserToGroup(groupId,userId);

        verify(groupMemberRepository).save(any(GroupMember.class));
    }

    @Test
    void testAddUserToGroup_UserExists() {
        Long groupId = 1l, userId = 3L;
        when(groupMemberRepository.existsById_GroupidAndId_Userid(groupId, userId)).thenReturn(true);
        when(userRepository.findById(3L)).thenReturn(Optional.of(new User(3L, "Raju", "raju@gmail.com", "9584321211","1")));

        ExpenseGroup group = new ExpenseGroup(groupId,"Trip");
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));

        Exception ex =  assertThrows(UserAlreadyExists.class,()->groupService.addUserToGroup(groupId, userId));
        assertTrue(ex.getMessage().contains("User already in group"));
    }

    @Test
    void testAddUserToGroup_UserNotFound() {
        Long groupId = 1l, userId = 3L;
        when(groupMemberRepository.existsById_GroupidAndId_Userid(groupId, userId)).thenReturn(false);
        when(userRepository.findById(3L)).thenReturn(Optional.empty());
        ExpenseGroup group = new ExpenseGroup(groupId,"Trip");
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        assertThrows(UserNotFound.class,()->groupService.addUserToGroup(groupId, userId));
    }

    @Test
    void testRemoveUserFromGroup_GroupNotFound(){
        Long groupId = 1L;
        ExpenseGroup group = new ExpenseGroup(groupId,"Trip");
        GroupMemberId memberId1 = new GroupMemberId(groupId,1L);
        GroupMember member1 = new GroupMember();
        member1.setId(memberId1);
        member1.setJoiningdate(new Timestamp(System.currentTimeMillis()));

        GroupMemberId memberId2 = new GroupMemberId(groupId,2L);
        GroupMember member2 = new GroupMember();
        member2.setId(memberId2);
        member2.setJoiningdate(new Timestamp(System.currentTimeMillis()));

        List<GroupMember> members = List.of(member1,member2);

        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());
        when(groupMemberRepository.findById_Groupid(groupId)).thenReturn(members);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "Aarush", "aarush@gmail.com", "9584321711","1")));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User(2L, "Parth", "parth@gmail.com", "9584320000","1")));
        assertThrows(GroupNotFound.class,()->groupService.removeUserFromGroup(groupId,2L));
    }

    @Test
    void testRemoveUserFromGroup_UserNotFound(){
        Long groupId = 1L;
        ExpenseGroup group = new ExpenseGroup(groupId,"Trip");
        GroupMemberId memberId1 = new GroupMemberId(groupId,1L);
        GroupMember member1 = new GroupMember();
        member1.setId(memberId1);
        member1.setJoiningdate(new Timestamp(System.currentTimeMillis()));

        GroupMemberId memberId2 = new GroupMemberId(groupId,2L);
        GroupMember member2 = new GroupMember();
        member2.setId(memberId2);
        member2.setJoiningdate(new Timestamp(System.currentTimeMillis()));

        GroupMemberId memberId3 = new GroupMemberId(groupId,3L);
        GroupMember member3 = new GroupMember();
        member3.setId(memberId3);
        member3.setJoiningdate(new Timestamp(System.currentTimeMillis()));

        List<GroupMember> members = List.of(member1,member2,member3);

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(groupMemberRepository.findById_Groupid(groupId)).thenReturn(members);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "Aarush", "aarush@gmail.com", "9584321711","1")));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User(2L, "Parth", "parth@gmail.com", "9584320000","1")));
        when(userRepository.findById(3L)).thenReturn(Optional.empty());
        Exception ex = assertThrows(UserNotFound.class,()->groupService.removeUserFromGroup(groupId,3L));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void testRemoveUserFromGroup_UserFound_NotPartOfGroup(){
        Long groupId = 1L;
        ExpenseGroup group = new ExpenseGroup(groupId,"Trip");
        GroupMemberId memberId1 = new GroupMemberId(groupId,1L);
        GroupMember member1 = new GroupMember();
        member1.setId(memberId1);
        member1.setJoiningdate(new Timestamp(System.currentTimeMillis()));

        GroupMemberId memberId2 = new GroupMemberId(groupId,2L);
        GroupMember member2 = new GroupMember();
        member2.setId(memberId2);
        member2.setJoiningdate(new Timestamp(System.currentTimeMillis()));

        List<GroupMember> members = List.of(member1,member2);

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(groupMemberRepository.findById_Groupid(groupId)).thenReturn(members);
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "Aarush", "aarush@gmail.com", "9584321711","1")));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User(2L, "Parth", "parth@gmail.com", "9584320000","1")));
        when(userRepository.findById(3L)).thenReturn(Optional.of(new User(3L,"Ravi","Ravi@gmail.com","34567","1")));
        Exception ex = assertThrows(UserNotInGroup.class,()->groupService.removeUserFromGroup(groupId,3L));
        assertTrue(ex.getMessage().contains("User is not part of the group"));
    }

    @Test
    void testRemoveUserFromGroup_NoDues() {
        Long groupid = 1L, userId = 2L;

        ExpenseGroup group = new ExpenseGroup(groupid, "Trip");

        User user = new User(userId, "Parth", "parth@gmail.com", "9584320000", "1");
        User otherUser = new User(1L, "Aarush", "aarush@gmail.com", "9584321711", "1");

        GroupMemberId groupMemberId = new GroupMemberId(groupid, userId);

        // Mock group and user existence
        when(groupRepository.findById(groupid)).thenReturn(Optional.of(group));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findById(1L)).thenReturn(Optional.of(otherUser));
        when(groupMemberRepository.existsById(groupMemberId)).thenReturn(true);

        // Mock balance check (no dues)
        when(expenseService.getBalances(groupid)).thenReturn(List.of());

        // Mock settlements
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        Settlement s1 = new Settlement();
        s1.setId(1L);
        s1.setGroupid(group);
        s1.setAmount(500.0);
        s1.setPaidby(user);      // Parth paid
        s1.setPaidto(otherUser); // Paid to Aarush
        s1.setDate(now);

        Settlement s2 = new Settlement();
        s2.setId(2L);
        s2.setGroupid(group);
        s2.setAmount(200.0);
        s2.setPaidby(otherUser);
        s2.setPaidto(user);
        s2.setDate(now);

        when(settlementRepository.findByGroupid_Groupid(groupid)).thenReturn(List.of(s1, s2));

        // Mock expenses
        Expense e1 = new Expense();
        e1.setExpenseid(1L);
        e1.setAmount(300.0);
        e1.setGroupid(group);
        e1.setUserid(user); // Paid by Parth

        Expense e2 = new Expense();
        e2.setExpenseid(2L);
        e2.setAmount(150.0);
        e2.setGroupid(group);
        e2.setUserid(otherUser); // Paid by Aarush

        when(expenseRepository.findByGroupid_Groupid(groupid)).thenReturn(List.of(e1, e2));

        // ✅ Run method
        assertDoesNotThrow(() -> groupService.removeUserFromGroup(groupid, userId));

        // ✅ Verifications
        verify(groupMemberRepository).deleteById(groupMemberId);
        verify(settlementRepository).deleteAll(anyList());
        verify(expenseRepository).deleteAll(anyList());
    }

    @Test
    void testRemoveUserFromGroup_WithDues(){
        Long groupid = 1L,userId=2L;
        ExpenseGroup group = new ExpenseGroup(groupid,"Trip");

        when(groupRepository.findById(groupid)).thenReturn(Optional.of(group));
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "Aarush", "aarush@gmail.com", "9584321711","1")));
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User(userId, "Parth", "parth@gmail.com", "9584320000","1")));
        when(expenseService.getBalances(groupid)).thenReturn(List.of("Aarush owes Parth ₹100"));

        GroupMemberId groupMemberId = new GroupMemberId(groupid, userId);
        when(groupMemberRepository.existsById(groupMemberId)).thenReturn(true);

        Exception ex = assertThrows(UserWithPendingDues.class,()-> groupService.removeUserFromGroup(groupid,userId));
        assertTrue(ex.getMessage().contains("Cannot remove user with unsettled balances."));

    }

    @Test
    void testGetGroupByUserId_Success(){
        Long groupId = 1L, userId = 1L;
        User user = new User(1L,"Aarush", "aarushg501@gmail.com","9584323333","1");
        ExpenseGroup group = new ExpenseGroup(groupId,"Trip");

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        GroupMember gp1 = new GroupMember(new GroupMemberId(groupId,userId),Timestamp.valueOf(LocalDateTime.now()),group,user);


        when(userRepository.existsById(userId)).thenReturn(true);
        when(groupMemberRepository.findById_Userid(userId)).thenReturn(List.of(gp1));

        List<ExpenseGroup> res = groupService.getGroupByUserId(userId);

        assertEquals(1,res.size());
        assertEquals(group,res.get(0));
    }

    @Test
    void testGetGroupByUserId_UserNotFound(){
        Long groupId = 1L, userId = 1L;
        User user = new User(1L,"Aarush", "aarushg501@gmail.com","9584323333","1");
        ExpenseGroup group = new ExpenseGroup(groupId,"Trip");

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        GroupMember gp1 = new GroupMember(new GroupMemberId(groupId,userId),Timestamp.valueOf(LocalDateTime.now()),group,user);


        when(userRepository.existsById(userId)).thenReturn(false);
        when(groupMemberRepository.findById_Userid(userId)).thenReturn(List.of(gp1));

        Exception ex = assertThrows(UserNotFound.class,()->groupService.getGroupByUserId(userId));
        assertTrue(ex.getMessage().contains("User not found"));

    }

    @Test
    void testGetGroupByUserId_NoGroupFoundException(){
        Long groupId = 1L, userId = 1L;
        User user = new User(1L,"Aarush", "aarushg501@gmail.com","9584323333","1");
        ExpenseGroup group = new ExpenseGroup(groupId,"Trip");

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        GroupMember gp1 = new GroupMember(new GroupMemberId(groupId,userId),Timestamp.valueOf(LocalDateTime.now()),group,user);


        when(userRepository.existsById(userId)).thenReturn(true);
        when(groupMemberRepository.findById_Userid(userId)).thenReturn(List.of());

        Exception ex = assertThrows(NoGroupFoundException.class,()->groupService.getGroupByUserId(userId));
        assertTrue(ex.getMessage().contains("not part of any group"));
    }

    @Test
    void deleteGroup_SuccessfulDeletiont() {
        Long groupid = 1L;
        ExpenseGroup group = new ExpenseGroup();
        group.setGroupid(groupid);

        Settlement settlement = new Settlement();
        Expense expense =new Expense();
        GroupMember groupMember = new GroupMember();

        when(groupRepository.findById(groupid)).thenReturn(Optional.of(group));
        when(expenseService.getBalances(groupid)).thenReturn(List.of("User A is settled with User B"));

        when(settlementRepository.findByGroupid_Groupid(groupid)).thenReturn(List.of(settlement));
        when(expenseRepository.findByGroupid_Groupid(groupid)).thenReturn(List.of(expense));
        when(groupMemberRepository.findById_Groupid(groupid)).thenReturn(List.of(groupMember));

        groupService.deleteGroup(groupid);

        verify(settlementRepository).deleteAll(any());
        verify(expenseRepository).deleteAll(any());
        verify(groupMemberRepository).deleteAll(any());
        verify(groupRepository).deleteById(groupid);

    }

    @Test
    void deleteGroup_GroupNotFound() {
        Long groupid = 2L;
        when(groupRepository.findById(groupid)).thenReturn(Optional.empty());

        Exception ex = assertThrows(GroupNotFound.class, () ->
                groupService.deleteGroup(groupid)
        );

        assertEquals("Group not found", ex.getMessage());

    }

    @Test
    void deleteGroup_HasPendingDues() {
        Long groupid = 3L;
        ExpenseGroup group = new ExpenseGroup();
        group.setGroupid(groupid);

        when(groupRepository.findById(groupid)).thenReturn(Optional.of(group));
        when(expenseService.getBalances(groupid)).thenReturn(List.of("User X owes User Y ₹500"));

        Exception ex = assertThrows(GroupHasPendingDuesException.class, () ->
                groupService.deleteGroup(groupid)
        );

        assertEquals("Cannot delete group with unsettled balances.", ex.getMessage());


        verify(settlementRepository, never()).deleteAll(any());
        verify(groupRepository, never()).deleteById(groupid);
    }





}

