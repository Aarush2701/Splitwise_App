package splitwise.splitwise.service;

import org.hibernate.validator.internal.engine.groups.Group;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import splitwise.splitwise.dto.UpdateSettlementRequest;
import splitwise.splitwise.exception.*;
import splitwise.splitwise.model.ExpenseGroup;
import splitwise.splitwise.model.Settlement;
import splitwise.splitwise.model.User;
import splitwise.splitwise.repository.ExpenseGroupRepository;
import splitwise.splitwise.repository.SettlementRepository;
import splitwise.splitwise.repository.UserRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SettlementServiceTest {

    @InjectMocks
    private SettlementService settlementService;

    @Mock
    private SettlementRepository settlementRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExpenseGroupRepository groupRepository;

    @Mock
    private ExpenseService expenseService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testCreateSettlement() {
        Long groupId = 1L, paidbyId = 2L, paidtoId = 3L;
        Double amount = 100.0;

        ExpenseGroup group = new ExpenseGroup(groupId, "Trip");
        User paidby = new User(paidbyId, "Parth", "parth@gmail.com", "958234567", "1");
        User paidto = new User(paidtoId, "Aarush", "aarush@gmail.com", "12345678", "1");

        when(userRepository.findById(paidbyId)).thenReturn(Optional.of(paidby));
        when(userRepository.findById(paidtoId)).thenReturn(Optional.of(paidto));
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(expenseService.getBalances(groupId)).thenReturn(List.of("Parth owes Aarush ₹1000.00"));
        when(expenseService.getBalanceBetweenUsers(groupId, paidbyId, paidtoId)).thenReturn(1000.0);

        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        Settlement settlement = new Settlement();
        settlement.setId(1L);
        settlement.setGroupid(group);
        settlement.setAmount(500.0);
        settlement.setPaidto(paidto);
        settlement.setPaidby(paidby);
        settlement.setDate(now);

        when(settlementRepository.save(any(Settlement.class))).thenReturn(settlement);

        Settlement result = settlementService.createSettlement(groupId, paidbyId, paidtoId, 500.0);

        assertNotNull(result);
        assertEquals(500.0, result.getAmount());
    }

    @Test
    void testCreateSettlement_SamePayerAndPayee_Fail() {
        Long groupId = 1L, userId = 2L;

        Exception ex = assertThrows(PayerAndPayeeSame.class, () -> settlementService.createSettlement(1L, 2L, 2L, 100.0));
        assertEquals("Payer and Payee cannot be the same user", ex.getMessage());
    }

    @Test
    void testCreateSettlement_PayerNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(UserNotFound.class,
                () -> settlementService.createSettlement(1L, 2L, 3L, 100.0));
        assertEquals("Payer not found", ex.getMessage());
    }

    @Test
    void testCreateSettlement_PayeeNotFound() {
        User paidby = new User(2L, "Parth", "parth@gmail.com", "958234567", "1");
        when(userRepository.findById(2L)).thenReturn(Optional.of(paidby));
        when(userRepository.findById(3L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(UserNotFound.class,
                () -> settlementService.createSettlement(1L, 2L, 3L, 100.0));
        assertEquals("Payee not found", ex.getMessage());
    }

    @Test
    void testCreateSettlement_GroupNotFound() {
        User paidby = new User(2L, "Parth", "parth@gmail.com", "958234567", "1");
        User paidto = new User(3L, "Aarush", "aarush@gmail.com", "12345678", "1");
        when(userRepository.findById(2L)).thenReturn(Optional.of(paidby));
        when(userRepository.findById(3L)).thenReturn(Optional.of(paidto));
        when(groupRepository.findById(1L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(GroupNotFound.class,
                () -> settlementService.createSettlement(1L, 2L, 3L, 100.0));
        assertEquals("Group not found", ex.getMessage());
    }

    @Test
    void createSettlement_AmountExceedsBalance() {
        Long groupId = 1L;
        Long paidById = 10L;
        Long paidToId = 20L;
        double amount = 150.0;
        double actualBalance = 100.0;

        User paidBy = new User();
        paidBy.setUserid(paidById);

        User paidTo = new User();
        paidTo.setUserid(paidToId);

        ExpenseGroup group = new ExpenseGroup();
        group.setGroupid(groupId);

        when(userRepository.findById(paidById)).thenReturn(Optional.of(paidBy));
        when(userRepository.findById(paidToId)).thenReturn(Optional.of(paidTo));
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(expenseService.getBalanceBetweenUsers(groupId, paidById, paidToId)).thenReturn(actualBalance);


        Exception ex = assertThrows(SettleAmountMoreThanDue.class, () -> {
            settlementService.createSettlement(groupId, paidById, paidToId, amount);
        });

        assertTrue(ex.getMessage().contains("Settlement amount exceeds dues. Due amount: "));

        verify(settlementRepository, never()).save(any());

    }


    @Test
    void testGetSettlementsByGroup_Success() {
        Long groupId = 1L;
        ExpenseGroup group = new ExpenseGroup(groupId, "Trip");

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(settlementRepository.findByGroupid_Groupid(groupId)).thenReturn(List.of(new Settlement()));

        List<Settlement> res = settlementService.getSettlementsByGroup(groupId);
        assertEquals(1, res.size());
    }

    @Test
    void testCreateSettlement_NoDuesFound() {
        Long groupId = 1L, paidbyId = 2L, paidtoId = 3L;
        Double amount = 100.0;

        ExpenseGroup group = new ExpenseGroup(groupId, "Trip");
        User paidby = new User(paidbyId, "Parth", "parth@gmail.com", "958234567", "1");
        User paidto = new User(paidtoId, "Aarush", "aarush@gmail.com", "12345678", "1");

        when(userRepository.findById(paidbyId)).thenReturn(Optional.of(paidby));
        when(userRepository.findById(paidtoId)).thenReturn(Optional.of(paidto));
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(expenseService.getBalances(groupId)).thenReturn(List.of("Parth owes Aarush ₹1000.00"));

        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        Settlement settlement = new Settlement();
        settlement.setId(1L);
        settlement.setGroupid(group);
        settlement.setAmount(500.0);
        settlement.setPaidto(paidto);
        settlement.setPaidby(paidby);
        settlement.setDate(now);

        when(settlementRepository.save(any(Settlement.class))).thenReturn(settlement);

        Exception ex = assertThrows(NoDuesExist.class, () -> settlementService.createSettlement(groupId, paidbyId, paidtoId, 500.0));

        assertEquals("No dues exist between these users", ex.getMessage());
    }

    @Test
    void testGetSettlementsByGroup_GroupNotFound() {
        when(groupRepository.findById(1L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(GroupNotFound.class, () -> settlementService.getSettlementsByGroup(1L));
        assertEquals("Group not found", ex.getMessage());
    }

    @Test
    void testGetSettlementsPaidByUser_Success() {
        Long groupId = 1L, userId = 2L;
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(new ExpenseGroup()));
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(settlementRepository.findByGroupid_GroupidAndPaidby_Userid(groupId, userId))
                .thenReturn(List.of(new Settlement()));

        List<Settlement> result = settlementService.getSettlementsPaidByUser(groupId, userId);
        assertEquals(1, result.size());
    }

    @Test
    void testGetSettlementsPaidByUser_GroupNotFound() {
        Long groupId = 1L;
        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        Exception ex = assertThrows(GroupNotFound.class, () -> settlementService.getSettlementsPaidByUser(groupId, 2L));

        assertEquals("Group not found", ex.getMessage());
    }

    @Test
    void testGetSettlementsPaidByUser_PayerNotFound() {
        Long groupId = 1L;
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(new ExpenseGroup()));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        Exception ex = assertThrows(UserNotFound.class, () -> settlementService.getSettlementsPaidByUser(groupId, 2L));

        assertEquals("Payer not found", ex.getMessage());
    }

    @Test
    void testGetSettlementsPaidToUser_Success() {
        Long groupId = 1L, userId = 2L;
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(new ExpenseGroup()));
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(settlementRepository.findByGroupid_GroupidAndPaidto_Userid(groupId, userId))
                .thenReturn(List.of(new Settlement()));

        List<Settlement> result = settlementService.getSettlementsPaidToUser(groupId, userId);
        assertEquals(1, result.size());
    }

    @Test
    void testGetSettlementsPaidToUser_GroupNotFound() {
        Long groupId = 1L;
        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        Exception ex = assertThrows(GroupNotFound.class, () -> settlementService.getSettlementsPaidToUser(groupId, 2L));

        assertEquals("Group not found", ex.getMessage());
    }

    @Test
    void testGetSettlementsPaidToUser_PayerNotFound() {
        Long groupId = 1L;
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(new ExpenseGroup()));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        Exception ex = assertThrows(UserNotFound.class, () -> settlementService.getSettlementsPaidToUser(groupId, 2L));

        assertEquals("Payee not found", ex.getMessage());
    }

    @Test
    void testDeleteSettlement_Success() {
        Long settleId = 1L;
        Settlement settlement = new Settlement();
        settlement.setId(settleId);

        when(settlementRepository.findById(settleId)).thenReturn(Optional.of(settlement));

        settlementService.deleteSettlement(settleId);

        verify(settlementRepository, times(1)).delete(settlement);
    }

    @Test
    void testDeleteSettlement_Fail_NoSettlementFound() {
        Long settleId = 1L;
        Settlement settlement = new Settlement();
        settlement.setId(settleId);

        when(settlementRepository.findById(settleId)).thenReturn(Optional.empty());

        Exception ex = assertThrows(SettlementNotFound.class, () -> settlementService.deleteSettlement(settleId));

        assertEquals("Settlement not found", ex.getMessage());
        verify(settlementRepository, never()).delete(any());

    }

    @Test
    void testUpdateSettlement_Success() {
        Long settlementId = 1L;
        Long paidById = 10L;
        Long paidToId = 20L;
        double oldAmount = 100.0;
        double newAmount = 80.0;
        double netDue = 150.0;

        ExpenseGroup group = new ExpenseGroup();
        group.setGroupid(99L);

        User paidBy = new User();
        paidBy.setUserid(paidById);

        User paidTo = new User();
        paidTo.setUserid(paidToId);

        Settlement existingSettlement = new Settlement();
        existingSettlement.setId(settlementId);
        existingSettlement.setAmount(oldAmount);
        existingSettlement.setGroupid(group);
        existingSettlement.setPaidby(paidBy);
        existingSettlement.setPaidto(paidTo);

        UpdateSettlementRequest request = new UpdateSettlementRequest();
        request.setPaidby(paidById);
        request.setPaidto(paidToId);
        request.setAmount(newAmount);

        when(settlementRepository.findById(settlementId)).thenReturn(Optional.of(existingSettlement));
        when(userRepository.findById(paidById)).thenReturn(Optional.of(paidBy));
        when(userRepository.findById(paidToId)).thenReturn(Optional.of(paidTo));
        when(expenseService.getBalanceBetweenUsers(group.getGroupid(), paidById, paidToId)).thenReturn(netDue);
        when(settlementRepository.save(any(Settlement.class))).thenAnswer(i -> i.getArgument(0));


        Settlement updated = settlementService.updateSettlement(settlementId, request);

        assertNotNull(updated);
        assertEquals(newAmount, updated.getAmount());
        assertEquals(paidBy, updated.getPaidby());
        assertEquals(paidTo, updated.getPaidto());
        verify(settlementRepository).save(updated);

    }

    @Test
    void testUpdateSettlement_SettlementNotFound() {
        when(settlementRepository.findById(anyLong())).thenReturn(Optional.empty());

        UpdateSettlementRequest request = new UpdateSettlementRequest();
        request.setPaidby(1L);
        request.setPaidto(2L);
        request.setAmount(50.0);

      Exception ex = assertThrows(SettlementNotFound.class, () ->
                settlementService.updateSettlement(1L, request)
        );

      assertEquals("Settlement not found",ex.getMessage());

    }

    @Test
    void testUpdateSettlement_PaidByUserNotFound() {
        Long settlementId = 1L;
        Settlement settlement = new Settlement();
        settlement.setGroupid(new ExpenseGroup());

        when(settlementRepository.findById(settlementId)).thenReturn(Optional.of(settlement));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UpdateSettlementRequest request = new UpdateSettlementRequest();
        request.setPaidby(1L);
        request.setPaidto(2L);
        request.setAmount(50.0);

       Exception ex = assertThrows(UserNotFound.class, () ->
                settlementService.updateSettlement(settlementId, request)
        );

       assertEquals("PaidBy user not found",ex.getMessage());

    }

    @Test
    void testUpdateSettlement_PaidToUserNotFound() {
        Long settlementId = 1L;

        User paidBy = new User();
        paidBy.setUserid(1L);

        Settlement settlement = new Settlement();
        settlement.setGroupid(new ExpenseGroup());

        when(settlementRepository.findById(settlementId)).thenReturn(Optional.of(settlement));
        when(userRepository.findById(1L)).thenReturn(Optional.of(paidBy));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        UpdateSettlementRequest request = new UpdateSettlementRequest();
        request.setPaidby(1L);
        request.setPaidto(2L);
        request.setAmount(50.0);

       Exception ex = assertThrows(UserNotFound.class, () ->
                settlementService.updateSettlement(settlementId, request)
        );

        assertEquals("PaidTo user not found",ex.getMessage());

    }

    @Test
    void testUpdateSettlement_AmountMoreThanDue() {
        Long settlementId = 1L;
        double oldAmount = 100.0;
        double netDue = 20.0;
        double requestedAmount = 130.0;

        ExpenseGroup group = new ExpenseGroup();
        group.setGroupid(1L);

        User paidBy = new User();
        paidBy.setUserid(1L);
        User paidTo = new User();
        paidTo.setUserid(2L);

        Settlement settlement = new Settlement();
        settlement.setAmount(oldAmount);
        settlement.setGroupid(group);

        UpdateSettlementRequest request = new UpdateSettlementRequest();
        request.setPaidby(1L);
        request.setPaidto(2L);
        request.setAmount(requestedAmount);

        when(settlementRepository.findById(settlementId)).thenReturn(Optional.of(settlement));
        when(userRepository.findById(1L)).thenReturn(Optional.of(paidBy));
        when(userRepository.findById(2L)).thenReturn(Optional.of(paidTo));
        when(expenseService.getBalanceBetweenUsers(1L, 1L, 2L)).thenReturn(netDue);

      Exception ex = assertThrows(SettleAmountMoreThanDue.class, () ->
                settlementService.updateSettlement(settlementId, request)
        );

        assertTrue(ex.getMessage().contains("Settlement amount exceeds dues. Due amount: "));

    }

}
