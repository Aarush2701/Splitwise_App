package splitwise.splitwise.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import splitwise.splitwise.dto.AddExpenseRequest;
import splitwise.splitwise.dto.ExpenseSplitResponse;
import splitwise.splitwise.enums.SplitType;
import splitwise.splitwise.exception.*;
import splitwise.splitwise.model.*;
import splitwise.splitwise.repository.*;
import splitwise.splitwise.strategy.SplitStrategy;
import splitwise.splitwise.strategy.SplitStrategyFactory;
import splitwise.splitwise.strategy.impl.EqualSplitStrategy;
import splitwise.splitwise.strategy.impl.ExactSplitStrategy;
import splitwise.splitwise.strategy.impl.PercentageSplitStrategy;

import javax.xml.transform.Result;
import java.sql.Array;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ExpenseServiceTest {

    @InjectMocks
    private ExpenseService expenseService;

    @Mock
    private ExpenseGroupRepository groupRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private ExpenseSplitRepository splitRepository;

    @Mock
    private SettlementRepository settlementRepository;

    @Mock
    private SplitStrategyFactory splitStrategyFactory;

    @Mock
    private SplitStrategy splitStrategy;

    @Mock
    private ExactSplitStrategy exactSplitStrategy;

    @Mock
    private GroupMemberRepository groupMemberRepository;

    @BeforeEach
    void init(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddExpense_EQUAL(){
        SplitStrategy strategy = new EqualSplitStrategy();
        Long groupid=1L , paidBy=2L;
        ExpenseGroup group = new ExpenseGroup(groupid,"Trip");
        List<Long> participants = List.of(1L,3L);

        AddExpenseRequest request= new AddExpenseRequest();
        request.setAmount(1000.0);
        request.setDescription("Food");
        request.setPaidBy(paidBy);
        request.setParticipants(participants);
        request.setSplitType(SplitType.EQUAL);

        User payer = new User(2L, "Parth", "parth@gmail.com", "9584320000","1");

        when(groupRepository.findById(groupid)).thenReturn(Optional.of(group));
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "Aarush", "aarush@gmail.com", "9584321711","1")));
        when(userRepository.findById(paidBy)).thenReturn(Optional.of(payer));
        when(userRepository.findById(3L)).thenReturn(Optional.of(new User(3L,"Ravi","Ravi@gmail.com","34567","1")));
        when(groupMemberRepository.existsById_GroupidAndId_Userid(groupid, 1L)).thenReturn(true);
        when(groupMemberRepository.existsById_GroupidAndId_Userid(groupid, 3L)).thenReturn(true);
        Expense savedExpense = new Expense();
        savedExpense.setExpenseid(1L);
        savedExpense.setAmount(1000.0);
        savedExpense.setDescription("Food");
        savedExpense.setGroupid(group);
        savedExpense.setUserid(payer);

        when(expenseRepository.save(any())).thenReturn(savedExpense);
        when(splitStrategyFactory.getStrategy(SplitType.EQUAL)).thenReturn(strategy);
        when(splitStrategy.calculateSplits(savedExpense,paidBy,participants,null))
                .thenReturn(List.of(new ExpenseSplit(savedExpense.getExpenseid(),1L,500.0),new ExpenseSplit(savedExpense.getExpenseid(),3L,500.0)));

       // assertDoesNotThrow(()->expenseService.addExpense(groupid,request));
        expenseService.addExpense(groupid,request);
        verify(splitRepository,times(1)).saveAll(any());
    }

    @Test
    void testAddExpense_EXACT(){
        SplitStrategy strategy = new ExactSplitStrategy();
        Long groupid=1L , paidBy=2L;
        ExpenseGroup group = new ExpenseGroup(groupid,"Trip");
        List<Long> participants = List.of(1L,3L);
        List<Double> value = List.of(400.0,600.0);

        AddExpenseRequest request= new AddExpenseRequest();
        request.setAmount(1000.0);
        request.setDescription("Food");
        request.setPaidBy(paidBy);
        request.setParticipants(participants);
        request.setSplitType(SplitType.EXACT);
        request.setValues(value);

        User payer = new User(2L, "Parth", "parth@gmail.com", "9584320000","1");

        when(groupRepository.findById(groupid)).thenReturn(Optional.of(group));
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "Aarush", "aarush@gmail.com", "9584321711","1")));
        when(userRepository.findById(paidBy)).thenReturn(Optional.of(payer));
        when(userRepository.findById(3L)).thenReturn(Optional.of(new User(3L,"Ravi","Ravi@gmail.com","34567","1")));
        when(groupMemberRepository.existsById_GroupidAndId_Userid(groupid, 1L)).thenReturn(true);
        when(groupMemberRepository.existsById_GroupidAndId_Userid(groupid, 3L)).thenReturn(true);
        Expense savedExpense = new Expense();
        savedExpense.setExpenseid(1L);
        savedExpense.setAmount(1000.0);
        savedExpense.setDescription("Food");
        savedExpense.setGroupid(group);
        savedExpense.setUserid(payer);

        when(expenseRepository.save(any())).thenReturn(savedExpense);
        when(splitStrategyFactory.getStrategy(SplitType.EXACT)).thenReturn(strategy);
        when(exactSplitStrategy.calculateSplits(savedExpense,paidBy,participants,value))
                .thenReturn(List.of(new ExpenseSplit(savedExpense.getExpenseid(),1L,400.0),new ExpenseSplit(savedExpense.getExpenseid(),3L,600.0)));

        // assertDoesNotThrow(()->expenseService.addExpense(groupid,request));
        expenseService.addExpense(groupid,request);
        verify(splitRepository,times(1)).saveAll(any());
    }

    @Test
    void testAddExpense_EXACT_Participants_and_exact_amount_count_mismatch(){
        SplitStrategy strategy = new ExactSplitStrategy();
        Long groupid=1L , paidBy=2L;
        ExpenseGroup group = new ExpenseGroup(groupid,"Trip");
        List<Long> participants = List.of(1L,3L);
        List<Double> value = List.of(400.0);
        when(groupMemberRepository.existsById_GroupidAndId_Userid(groupid, 1L)).thenReturn(true);
        when(groupMemberRepository.existsById_GroupidAndId_Userid(groupid, 3L)).thenReturn(true);
        AddExpenseRequest request= new AddExpenseRequest();
        request.setAmount(1000.0);
        request.setDescription("Food");
        request.setPaidBy(paidBy);
        request.setParticipants(participants);
        request.setSplitType(SplitType.EXACT);
        request.setValues(value);

        User payer = new User(2L, "Parth", "parth@gmail.com", "9584320000","1");

        when(groupRepository.findById(groupid)).thenReturn(Optional.of(group));
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "Aarush", "aarush@gmail.com", "9584321711","1")));
        when(userRepository.findById(paidBy)).thenReturn(Optional.of(payer));
        when(userRepository.findById(3L)).thenReturn(Optional.of(new User(3L,"Ravi","Ravi@gmail.com","34567","1")));

        Expense savedExpense = new Expense();
        savedExpense.setExpenseid(1L);
        savedExpense.setAmount(1000.0);
        savedExpense.setDescription("Food");
        savedExpense.setGroupid(group);
        savedExpense.setUserid(payer);

        when(expenseRepository.save(any())).thenReturn(savedExpense);
        when(splitStrategyFactory.getStrategy(SplitType.EXACT)).thenReturn(strategy);
        when(exactSplitStrategy.calculateSplits(savedExpense,paidBy,participants,value))
                .thenReturn(List.of(new ExpenseSplit(savedExpense.getExpenseid(),1L,400.0),new ExpenseSplit(savedExpense.getExpenseid(),3L,600.0)));

        // assertDoesNotThrow(()->expenseService.addExpense(groupid,request));
       Exception ex = assertThrows(ParticipantCountMismatch.class,()->expenseService.addExpense(groupid,request));
       assertEquals(ex.getMessage(),"Participants and exact amount count mismatch.");
    }

    @Test
    void testAddExpense_EXACT_Exact_amounts_do_not_sum_to_total_expense(){
        SplitStrategy strategy = new ExactSplitStrategy();
        Long groupid=1L , paidBy=2L;
        ExpenseGroup group = new ExpenseGroup(groupid,"Trip");
        List<Long> participants = List.of(1L,3L);
        List<Double> value = List.of(400.0,6000.0);
        when(groupMemberRepository.existsById_GroupidAndId_Userid(groupid, 1L)).thenReturn(true);
        when(groupMemberRepository.existsById_GroupidAndId_Userid(groupid, 3L)).thenReturn(true);
        AddExpenseRequest request= new AddExpenseRequest();
        request.setAmount(1000.0);
        request.setDescription("Food");
        request.setPaidBy(paidBy);
        request.setParticipants(participants);
        request.setSplitType(SplitType.EXACT);
        request.setValues(value);

        User payer = new User(2L, "Parth", "parth@gmail.com", "9584320000","1");

        when(groupRepository.findById(groupid)).thenReturn(Optional.of(group));
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "Aarush", "aarush@gmail.com", "9584321711","1")));
        when(userRepository.findById(paidBy)).thenReturn(Optional.of(payer));
        when(userRepository.findById(3L)).thenReturn(Optional.of(new User(3L,"Ravi","Ravi@gmail.com","34567","1")));

        Expense savedExpense = new Expense();
        savedExpense.setExpenseid(1L);
        savedExpense.setAmount(1000.0);
        savedExpense.setDescription("Food");
        savedExpense.setGroupid(group);
        savedExpense.setUserid(payer);

        when(expenseRepository.save(any())).thenReturn(savedExpense);
        when(splitStrategyFactory.getStrategy(SplitType.EXACT)).thenReturn(strategy);
        when(exactSplitStrategy.calculateSplits(savedExpense,paidBy,participants,value))
                .thenReturn(List.of(new ExpenseSplit(savedExpense.getExpenseid(),1L,400.0),new ExpenseSplit(savedExpense.getExpenseid(),3L,600.0)));

        // assertDoesNotThrow(()->expenseService.addExpense(groupid,request));
        Exception ex = assertThrows(ExactAmountSum.class,()->expenseService.addExpense(groupid,request));
        assertEquals(ex.getMessage(),"Exact amounts do not sum to total expense.");
    }

    @Test
    void testAddExpense_PERCENTAGE(){
        SplitStrategy strategy = new PercentageSplitStrategy();
        Long groupid=1L , paidBy=2L;
        ExpenseGroup group = new ExpenseGroup(groupid,"Trip");
        List<Long> participants = List.of(1L,3L);
        List<Double> percentage = List.of(40.0,60.0);

        AddExpenseRequest request= new AddExpenseRequest();
        request.setAmount(1000.0);
        request.setDescription("Food");
        request.setPaidBy(paidBy);
        request.setParticipants(participants);
        request.setSplitType(SplitType.PERCENTAGE);
        request.setValues(percentage);

        User payer = new User(2L, "Parth", "parth@gmail.com", "9584320000","1");

        when(groupRepository.findById(groupid)).thenReturn(Optional.of(group));
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "Aarush", "aarush@gmail.com", "9584321711","1")));
        when(userRepository.findById(paidBy)).thenReturn(Optional.of(payer));
        when(userRepository.findById(3L)).thenReturn(Optional.of(new User(3L,"Ravi","Ravi@gmail.com","34567","1")));
        when(groupMemberRepository.existsById_GroupidAndId_Userid(groupid, 1L)).thenReturn(true);
        when(groupMemberRepository.existsById_GroupidAndId_Userid(groupid, 3L)).thenReturn(true);
        Expense savedExpense = new Expense();
        savedExpense.setExpenseid(1L);
        savedExpense.setAmount(1000.0);
        savedExpense.setDescription("Food");
        savedExpense.setGroupid(group);
        savedExpense.setUserid(payer);

        when(expenseRepository.save(any())).thenReturn(savedExpense);
        when(splitStrategyFactory.getStrategy(SplitType.PERCENTAGE)).thenReturn(strategy);
        when(splitStrategy.calculateSplits(savedExpense,paidBy,participants,percentage))
                .thenReturn(List.of(new ExpenseSplit(savedExpense.getExpenseid(),1L,400.0),new ExpenseSplit(savedExpense.getExpenseid(),3L,600.0)));

        // assertDoesNotThrow(()->expenseService.addExpense(groupid,request));
        expenseService.addExpense(groupid,request);
        verify(splitRepository,times(1)).saveAll(any());
    }

    @Test
    void testAddExpense_PERCENTAGE_Participants_and_percentages_count_mismatch(){
        SplitStrategy strategy = new PercentageSplitStrategy();
        Long groupid=1L , paidBy=2L;
        ExpenseGroup group = new ExpenseGroup(groupid,"Trip");
        List<Long> participants = List.of(1L,3L);
        List<Double> percentage = List.of(40.0);

        AddExpenseRequest request= new AddExpenseRequest();
        request.setAmount(1000.0);
        request.setDescription("Food");
        request.setPaidBy(paidBy);
        request.setParticipants(participants);
        request.setSplitType(SplitType.PERCENTAGE);
        request.setValues(percentage);

        User payer = new User(2L, "Parth", "parth@gmail.com", "9584320000","1");

        when(groupRepository.findById(groupid)).thenReturn(Optional.of(group));
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "Aarush", "aarush@gmail.com", "9584321711","1")));
        when(userRepository.findById(paidBy)).thenReturn(Optional.of(payer));
        when(userRepository.findById(3L)).thenReturn(Optional.of(new User(3L,"Ravi","Ravi@gmail.com","34567","1")));
        when(groupMemberRepository.existsById_GroupidAndId_Userid(groupid, 1L)).thenReturn(true);
        when(groupMemberRepository.existsById_GroupidAndId_Userid(groupid, 3L)).thenReturn(true);
        Expense savedExpense = new Expense();
        savedExpense.setExpenseid(1L);
        savedExpense.setAmount(1000.0);
        savedExpense.setDescription("Food");
        savedExpense.setGroupid(group);
        savedExpense.setUserid(payer);

        when(expenseRepository.save(any())).thenReturn(savedExpense);
        when(splitStrategyFactory.getStrategy(SplitType.PERCENTAGE)).thenReturn(strategy);
        when(splitStrategy.calculateSplits(savedExpense,paidBy,participants,percentage))
                .thenReturn(List.of(new ExpenseSplit(savedExpense.getExpenseid(),1L,400.0),new ExpenseSplit(savedExpense.getExpenseid(),3L,600.0)));

        Exception ex = assertThrows(ParticipantCountMismatch.class,()->expenseService.addExpense(groupid,request));
        assertEquals(ex.getMessage(),"Participants and percentages count mismatch.");
    }

    @Test
    void testAddExpense_PERCENTAGE_SUM_NOT_100(){
        SplitStrategy strategy = new PercentageSplitStrategy();
        Long groupid=1L , paidBy=2L;
        ExpenseGroup group = new ExpenseGroup(groupid,"Trip");
        List<Long> participants = List.of(1L,3L);
        List<Double> percentage = List.of(40.0,55.0);

        AddExpenseRequest request= new AddExpenseRequest();
        request.setAmount(1000.0);
        request.setDescription("Food");
        request.setPaidBy(paidBy);
        request.setParticipants(participants);
        request.setSplitType(SplitType.PERCENTAGE);
        request.setValues(percentage);

        User payer = new User(2L, "Parth", "parth@gmail.com", "9584320000","1");

        when(groupRepository.findById(groupid)).thenReturn(Optional.of(group));
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "Aarush", "aarush@gmail.com", "9584321711","1")));
        when(userRepository.findById(paidBy)).thenReturn(Optional.of(payer));
        when(userRepository.findById(3L)).thenReturn(Optional.of(new User(3L,"Ravi","Ravi@gmail.com","34567","1")));
        when(groupMemberRepository.existsById_GroupidAndId_Userid(groupid, 1L)).thenReturn(true);
        when(groupMemberRepository.existsById_GroupidAndId_Userid(groupid, 3L)).thenReturn(true);
        Expense savedExpense = new Expense();
        savedExpense.setExpenseid(1L);
        savedExpense.setAmount(1000.0);
        savedExpense.setDescription("Food");
        savedExpense.setGroupid(group);
        savedExpense.setUserid(payer);

        when(expenseRepository.save(any())).thenReturn(savedExpense);
        when(splitStrategyFactory.getStrategy(SplitType.PERCENTAGE)).thenReturn(strategy);
        when(splitStrategy.calculateSplits(savedExpense,paidBy,participants,percentage))
                .thenReturn(List.of(new ExpenseSplit(savedExpense.getExpenseid(),1L,400.0),new ExpenseSplit(savedExpense.getExpenseid(),3L,600.0)));

        Exception ex = assertThrows(PercentageSumNot100.class,()->expenseService.addExpense(groupid,request));
        assertEquals(ex.getMessage(),"Percentages must sum to 100.");

    }

    @Test
    void testAddExpense_GroupNotFound(){
        Long groupid=1L , paidBy=2L;
        ExpenseGroup group = new ExpenseGroup(groupid,"Trip");
        List<Long> participants = List.of(1L,3L);

        AddExpenseRequest request= new AddExpenseRequest();
        request.setAmount(1000.0);
        request.setDescription("Food");
        request.setPaidBy(paidBy);
        request.setParticipants(participants);

        User payer = new User(2L, "Parth", "parth@gmail.com", "9584320000","1");

        when(groupRepository.findById(groupid)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "Aarush", "aarush@gmail.com", "9584321711","1")));
        when(userRepository.findById(paidBy)).thenReturn(Optional.of(payer));
        when(userRepository.findById(3L)).thenReturn(Optional.of(new User(3L,"Ravi","Ravi@gmail.com","34567","1")));

        Expense savedExpense = new Expense();
        savedExpense.setExpenseid(1L);
        savedExpense.setAmount(1000.0);
        savedExpense.setDescription("Food");
        savedExpense.setGroupid(group);
        savedExpense.setUserid(payer);

        when(expenseRepository.save(any())).thenReturn(savedExpense);
        Exception ex =  assertThrows(GroupNotFound.class,()->expenseService.addExpense(groupid,request));
        assertEquals("Group not found",ex.getMessage());
    }

    @Test
    void testAddExpense_PayerNotFound(){
        Long groupid=1L , paidBy=2L;
        ExpenseGroup group = new ExpenseGroup(groupid,"Trip");
        List<Long> participants = List.of(1L,3L);

        AddExpenseRequest request= new AddExpenseRequest();
        request.setAmount(1000.0);
        request.setDescription("Food");
        request.setPaidBy(paidBy);
        request.setParticipants(participants);

        User payer = new User(2L, "Parth", "parth@gmail.com", "9584320000","1");

        when(groupRepository.findById(groupid)).thenReturn(Optional.of(group));
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "Aarush", "aarush@gmail.com", "9584321711","1")));
        when(userRepository.findById(paidBy)).thenReturn(Optional.empty());
        when(userRepository.findById(3L)).thenReturn(Optional.of(new User(3L,"Ravi","Ravi@gmail.com","34567","1")));

        Expense savedExpense = new Expense();
        savedExpense.setExpenseid(1L);
        savedExpense.setAmount(1000.0);
        savedExpense.setDescription("Food");
        savedExpense.setGroupid(group);
        savedExpense.setUserid(payer);

        when(expenseRepository.save(any())).thenReturn(savedExpense);
       Exception ex =  assertThrows(UserNotFound.class,()->expenseService.addExpense(groupid,request));
        assertEquals("User not found",ex.getMessage());
    }

    @Test
    void testAddExpense_ParticipantNotFound(){
        Long groupid=1L , paidBy=2L;
        ExpenseGroup group = new ExpenseGroup(groupid,"Trip");
        List<Long> participants = List.of(1L,3L);

        AddExpenseRequest request= new AddExpenseRequest();
        request.setAmount(1000.0);
        request.setDescription("Food");
        request.setPaidBy(paidBy);
        request.setParticipants(participants);

        User payer = new User(2L, "Parth", "parth@gmail.com", "9584320000","1");
        when(groupMemberRepository.existsById_GroupidAndId_Userid(groupid, 1L)).thenReturn(true);
        when(groupMemberRepository.existsById_GroupidAndId_Userid(groupid, 3L)).thenReturn(true);
        when(groupRepository.findById(groupid)).thenReturn(Optional.of(group));
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "Aarush", "aarush@gmail.com", "9584321711","1")));
        when(userRepository.findById(paidBy)).thenReturn(Optional.of(payer));
        when(userRepository.findById(3L)).thenReturn(Optional.empty());

        Expense savedExpense = new Expense();
        savedExpense.setExpenseid(1L);
        savedExpense.setAmount(1000.0);
        savedExpense.setDescription("Food");
        savedExpense.setGroupid(group);
        savedExpense.setUserid(payer);

        when(expenseRepository.save(any())).thenReturn(savedExpense);
         Exception ex = assertThrows(UserNotFound.class,()->expenseService.addExpense(groupid,request));
         assertTrue(ex.getMessage().contains("not found"));
    }

    @Test
    void testGetExpensesByGroup(){
        Long groupid = 1L;
        ExpenseGroup group = new ExpenseGroup(groupid, "Trip");
        User payer = new User(2L, "Parth", "parth@gmail.com", "9584320000","1");

        Expense savedExpense = new Expense();
        savedExpense.setExpenseid(1L);
        savedExpense.setAmount(1000.0);
        savedExpense.setDescription("Food");
        savedExpense.setGroupid(group);
        savedExpense.setUserid(payer);
        when(groupRepository.findById(groupid)).thenReturn(Optional.of(group));

        when(expenseRepository.findByGroupid_Groupid(groupid)).thenReturn(List.of(savedExpense));
        List<Expense> result = expenseService.getExpensesByGroup(groupid);
        assertEquals(1, result.size());
    }

    @Test
    void testGetExpensesByGroupAndUser(){
        Long groupid = 1L, userId = 2L;
        ExpenseGroup group = new ExpenseGroup(groupid, "Trip");
        User payer = new User(userId, "Parth", "parth@gmail.com", "9584320000","1");

        Expense savedExpense = new Expense();
        savedExpense.setExpenseid(1L);
        savedExpense.setAmount(1000.0);
        savedExpense.setDescription("Food");
        savedExpense.setGroupid(group);
        savedExpense.setUserid(payer);
        when(userRepository.findById(2L)).thenReturn(Optional.of(payer));
        when(groupRepository.findById(groupid)).thenReturn(Optional.of(group));
        when(expenseRepository.findByGroupid_GroupidAndUserid_Userid(groupid,userId)).thenReturn(List.of(savedExpense));
        List<Expense> result = expenseService.getExpensesByGroupAndUser(groupid,userId);
        assertEquals(1, result.size());
    }

    @Test
    void testGetBalance(){
        Long groupid= 1L;
        ExpenseGroup group = new ExpenseGroup(groupid,"Trip");
        User user1 = new User(1L, "Parth", "parth@gmail.com", "9584320000","1");
        User user2 = new User(2L,"Aarush", "aarush@gmail.com","9584324441","1");
        User user3 = new User(3L,"Vicky","vicks@gmail.com","3253792537","1");

        when(groupRepository.findById(groupid)).thenReturn(Optional.of(group));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(userRepository.findById(3L)).thenReturn(Optional.of(user3));

        Timestamp now = new Timestamp(System.currentTimeMillis());
        Expense expense1 = new Expense(1L,800.0,"Lunch",now,user1,group);

        ExpenseSplit split1 = new ExpenseSplit(1L,2L,400.0);
        ExpenseSplit split2 = new ExpenseSplit(1L,3L,400.0);


        when(expenseRepository.findByGroupid_Groupid(groupid)).thenReturn(List.of(expense1));
        when(splitRepository.findByExpenseid(1L)).thenReturn(List.of(split1,split2));

        List<String> balances = expenseService.getBalances(groupid);
        assertEquals(2,balances.size());
        assertEquals("Vicky owes Parth ₹400.00",balances.get(0));
        assertEquals("Aarush owes Parth ₹400.00",balances.get(1));
    }

    @Test
    void testGetBalance_NoExpenses(){
        when(groupRepository.findById(1L)).thenReturn(Optional.of(new ExpenseGroup(1L,"Trip")));
        when(expenseRepository.findByGroupid_Groupid(1L)).thenReturn(List.of());

        List<String> result = expenseService.getBalances(1L);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetBalance_ThreeUsers_WithNetCalculation(){
            Long groupid= 1L;
            ExpenseGroup group = new ExpenseGroup(groupid,"Trip");
            User user1 = new User(1L, "Parth", "parth@gmail.com", "9584320000","1");
            User user2 = new User(2L,"Aarush", "aarush@gmail.com","9584324441","1");
            User user3 = new User(3L,"Vicky","vicks@gmail.com","3253792537","1");

            when(groupRepository.findById(groupid)).thenReturn(Optional.of(group));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
            when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
            when(userRepository.findById(3L)).thenReturn(Optional.of(user3));

            Timestamp now = new Timestamp(System.currentTimeMillis());
            Expense expense1 = new Expense(1L,300.0,"Lunch",now,user1,group);
            Expense expense2 = new Expense(2L,200.0,"Snacks",now,user3,group);
            ExpenseSplit split1 = new ExpenseSplit(1L,2L,150.0);
            ExpenseSplit split2 = new ExpenseSplit(1L,3L,150.0);

            ExpenseSplit split3 = new ExpenseSplit(2L,1L,100.0);



            when(expenseRepository.findByGroupid_Groupid(groupid)).thenReturn(List.of(expense1,expense2));
            when(splitRepository.findByExpenseid(1L)).thenReturn(List.of(split1,split2));
            when(splitRepository.findByExpenseid(2L)).thenReturn(List.of(split3));

            List<String> balances = expenseService.getBalances(groupid);
            assertEquals(2,balances.size());
            assertEquals("Aarush owes Parth ₹150.00",balances.get(1));
    }

    @Test
    void testGetUserBalance(){
        Long userId = 2L, groupid = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User(userId,"Aarush", "aarush@gmail.com","9584324441","1")));
        when(expenseRepository.findByGroupid_Groupid(groupid)).thenReturn(List.of());
        when(settlementRepository.findByGroupid_Groupid(groupid)).thenReturn(List.of());

        List<String> balances = expenseService.getUserBalance(groupid,userId);
        assertTrue(balances.isEmpty());

    }

    @Test
    void testGetUserBalance_withExpenseOnly(){
        Long  groupid = 1L, payerId=1L, userId = 2L;
        ExpenseGroup group = new ExpenseGroup(groupid, "Trip");
        User payer = new User(payerId, "Parth", "parth@gmail.com", "9584320000","1");
        User debtor = new User(userId,"Aarush", "aarush@gmail.com","9584324441","1");
        Timestamp now = new Timestamp(System.currentTimeMillis());

        Expense expense = new Expense(1L,300.0,"Food",now,payer,group);

        ExpenseSplit split = new ExpenseSplit(1L,userId,150.0);

        when(groupRepository.findById(groupid)).thenReturn(Optional.of(group));
        when(userRepository.findById(payerId)).thenReturn(Optional.of(payer));
        when(userRepository.findById(userId)).thenReturn(Optional.of(debtor));
        when(expenseRepository.findByGroupid_Groupid(groupid)).thenReturn(List.of(expense));
        when(splitRepository.findByExpenseid(1L)).thenReturn(List.of(split));
        when(settlementRepository.findByGroupid_Groupid(groupid)).thenReturn(List.of());



        List<String> balances = expenseService.getUserBalance(groupid,userId);
        assertEquals(1,balances.size());
        assertTrue(balances.get(0).contains("Aarush owes Parth ₹150.00"));
    }

    @Test
    void testGetUserBalance_withExpenseAndPartialSettlement(){
        Long  groupid = 1L, payerId=1L, userId = 2L;
        ExpenseGroup group = new ExpenseGroup(groupid, "Trip");
        User payer = new User(payerId, "Parth", "parth@gmail.com", "9584320000","1");
        User debtor = new User(userId,"Aarush", "aarush@gmail.com","9584324441","1");
        Timestamp now = new Timestamp(System.currentTimeMillis());

        Expense expense = new Expense(1L,300.0,"Food",now,payer,group);

        ExpenseSplit split = new ExpenseSplit(1L,userId,150.0);

        Settlement settlement = new Settlement(1L,group,debtor,payer,100.0,now);

        when(groupRepository.findById(groupid)).thenReturn(Optional.of(group));
        when(userRepository.findById(payerId)).thenReturn(Optional.of(payer));
        when(userRepository.findById(userId)).thenReturn(Optional.of(debtor));
        when(expenseRepository.findByGroupid_Groupid(groupid)).thenReturn(List.of(expense));
        when(splitRepository.findByExpenseid(1L)).thenReturn(List.of(split));
        when(settlementRepository.findByGroupid_Groupid(groupid)).thenReturn(List.of(settlement));


        List<String> balances = expenseService.getUserBalance(groupid,userId);
        assertEquals(1,balances.size());
        assertEquals(balances.get(0),"Aarush owes Parth ₹50.00");
        assertTrue(balances.get(0).contains("Aarush owes Parth ₹50.00"));
    }

    @Test
    void testGetUserBalance_withExpenseAndFullSettlement(){
        Long  groupid = 1L, payerId=1L, userId = 2L;
        ExpenseGroup group = new ExpenseGroup(groupid, "Trip");
        User payer = new User(payerId, "Parth", "parth@gmail.com", "9584320000","1");
        User debtor = new User(userId,"Aarush", "aarush@gmail.com","9584324441","1");
        Timestamp now = new Timestamp(System.currentTimeMillis());

        Expense expense = new Expense(1L,300.0,"Food",now,payer,group);

        ExpenseSplit split = new ExpenseSplit(1L,userId,150.0);

        Settlement settlement = new Settlement(1L,group,debtor,payer,150.0,now);

        when(groupRepository.findById(groupid)).thenReturn(Optional.of(group));
        when(userRepository.findById(payerId)).thenReturn(Optional.of(payer));
        when(userRepository.findById(userId)).thenReturn(Optional.of(debtor));
        when(expenseRepository.findByGroupid_Groupid(groupid)).thenReturn(List.of(expense));
        when(splitRepository.findByExpenseid(1L)).thenReturn(List.of(split));
        when(settlementRepository.findByGroupid_Groupid(groupid)).thenReturn(List.of(settlement));



        List<String> balances = expenseService.getUserBalance(groupid,userId);
        assertEquals(0,balances.size());
    }

    @Test
    void testGetUserBalance_withDoubleSidedExpense(){
        Long  groupid = 1L, payerId=1L, userId = 2L;
        ExpenseGroup group = new ExpenseGroup(groupid, "Trip");
        User payer = new User(payerId, "Parth", "parth@gmail.com", "9584320000","1");
        User debtor = new User(userId,"Aarush", "aarush@gmail.com","9584324441","1");
        Timestamp now = new Timestamp(System.currentTimeMillis());

        Expense expense1 = new Expense(1L,300.0,"Food",now,payer,group);
        ExpenseSplit split1 = new ExpenseSplit(1L,userId,150.0);
        Expense expense2 = new Expense(2L,200.0,"Food",now,debtor,group);
        ExpenseSplit split2 = new ExpenseSplit(2L,payerId,100.0);

        List<Expense> expenseList=List.of(expense1,expense2);
        when(groupRepository.findById(groupid)).thenReturn(Optional.of(group));
        when(settlementRepository.findByGroupid_Groupid(groupid)).thenReturn(List.of());
        when(expenseRepository.findByGroupid_Groupid(groupid)).thenReturn(expenseList);

        when(splitRepository.findByExpenseid(1L)).thenReturn(List.of(split1));
        when(splitRepository.findByExpenseid(2L)).thenReturn(List.of(split2));

        when(userRepository.findById(payerId)).thenReturn(Optional.of(payer));
        when(userRepository.findById(userId)).thenReturn(Optional.of(debtor));



        List<String> balances = expenseService.getUserBalance(groupid,userId);
        assertEquals(1,balances.size());
        assertEquals("Aarush owes Parth ₹50.00",balances.get(0));
    }

    @Test
    void testGetUserBalance_withDoubleSidedExpenseAndSettlement(){
        Long  groupid = 1L, payerId=1L, userId = 2L;
        ExpenseGroup group = new ExpenseGroup(groupid, "Trip");
        User payer = new User(payerId, "Parth", "parth@gmail.com", "9584320000","1");
        User debtor = new User(userId,"Aarush", "aarush@gmail.com","9584324441","1");
        Timestamp now = new Timestamp(System.currentTimeMillis());

        Expense expense1 = new Expense(1L,300.0,"Food",now,payer,group);
        ExpenseSplit split1 = new ExpenseSplit(1L,userId,150.0);
        Expense expense2 = new Expense(2L,200.0,"Food",now,debtor,group);
        ExpenseSplit split2 = new ExpenseSplit(2L,payerId,100.0);

        Settlement settlement = new Settlement(1L,group,debtor,payer,20.0,now);

        List<Expense> expenseList=List.of(expense1,expense2);
        when(groupRepository.findById(groupid)).thenReturn(Optional.of(group));
        when(settlementRepository.findByGroupid_Groupid(groupid)).thenReturn(List.of(settlement));
        when(expenseRepository.findByGroupid_Groupid(groupid)).thenReturn(expenseList);

        when(splitRepository.findByExpenseid(1L)).thenReturn(List.of(split1));
        when(splitRepository.findByExpenseid(2L)).thenReturn(List.of(split2));

        when(userRepository.findById(payerId)).thenReturn(Optional.of(payer));
        when(userRepository.findById(userId)).thenReturn(Optional.of(debtor));



        List<String> balances = expenseService.getUserBalance(groupid,userId);
        assertEquals(1,balances.size());
        assertEquals("Aarush owes Parth ₹30.00",balances.get(0));
    }

    @Test
    void testGetBalanceBetweenUsers_User1OwesUser2(){
        Long groupid = 1L , user1Id = 101L, user2Id = 102L;

        User user1 = new User(user1Id,"Parth","parth@gmail.com","3456789","1");
        User user2 = new User(user2Id,"Aarush","aaru@gmail.com","12345678","1");
        ExpenseGroup group = new ExpenseGroup(groupid, "Trip");

        Expense expense = new Expense();
        expense.setExpenseid(1L);
        expense.setAmount(300.0);
        expense.setGroupid(group);
        expense.setUserid(user2);

        ExpenseSplit split = new ExpenseSplit(1L,user1Id,150.0);

        when(userRepository.findById(user1Id)).thenReturn(Optional.of(user1));
        when(userRepository.findById(user2Id)).thenReturn(Optional.of(user2));

        when(expenseRepository.findByGroupid_Groupid(groupid)).thenReturn(List.of(expense));
        when(settlementRepository.findByGroupid_Groupid(groupid)).thenReturn(List.of());
        when(splitRepository.findByExpenseid(any())).thenReturn(List.of(split));


        double balance = expenseService.getBalanceBetweenUsers(groupid,user1Id,user2Id);
        assertEquals(150.0,balance);
    }

    @Test
    void testGetBalanceBetweenUsers_User2OwesUser1(){
        Long groupid = 1L , user1Id = 101L, user2Id = 102L;

        User user1 = new User(user1Id,"Parth","parth@gmail.com","3456789","1");
        User user2 = new User(user2Id,"Aarush","aaru@gmail.com","12345678","1");
        ExpenseGroup group = new ExpenseGroup(groupid,"Trip");

        Expense expense = new Expense();
        expense.setExpenseid(1L);
        expense.setAmount(500.0);
        expense.setGroupid(group);
        expense.setUserid(user1);

        ExpenseSplit split = new ExpenseSplit(1L,user2Id,250.0);

        when(userRepository.findById(user1Id)).thenReturn(Optional.of(user1));
        when(userRepository.findById(user2Id)).thenReturn(Optional.of(user2));

        when(expenseRepository.findByGroupid_Groupid(groupid)).thenReturn(List.of(expense));
        when(settlementRepository.findByGroupid_Groupid(groupid)).thenReturn(List.of());
        when(splitRepository.findByExpenseid(any())).thenReturn(List.of(split));


        double balance = expenseService.getBalanceBetweenUsers(groupid,user1Id,user2Id);
        assertEquals(-250.0,balance);
    }

    @Test
    void testGetBalanceBetweenUsers_NoDues(){
        Long groupid = 1L , user1Id = 101L, user2Id = 102L;

        User user1 = new User(user1Id,"Parth","parth@gmail.com","3456789","1");
        User user2 = new User(user2Id,"Aarush","aaru@gmail.com","12345678","1");

        when(userRepository.findById(user1Id)).thenReturn(Optional.of(user1));
        when(userRepository.findById(user2Id)).thenReturn(Optional.of(user2));

        when(expenseRepository.findByGroupid_Groupid(groupid)).thenReturn(List.of());
        when(settlementRepository.findByGroupid_Groupid(groupid)).thenReturn(List.of());
        when(splitRepository.findByExpenseid(any())).thenReturn(List.of());

        double balance = expenseService.getBalanceBetweenUsers(groupid,user1Id,user2Id);
        assertEquals(0.0,balance);
    }

    @Test
    void deleteExpense_Success(){
        Long groupid= 1L;
        Long expenseid = 100L;

        Expense expense = new Expense();
        expense.setExpenseid(expenseid);

        when(expenseRepository.findById(expenseid)).thenReturn(Optional.of(expense));

        expenseService.deleteExpense(groupid,expenseid);

        verify(splitRepository).deleteByExpenseid(expenseid);
        verify(expenseRepository).delete(expense);

    }

    @Test
    void deleteExpense_ExpenseNotFound(){
        Long groupid= 1L;
        Long expenseid = 100L;

        when(expenseRepository.findById(expenseid)).thenReturn(Optional.empty());

        Exception ex = assertThrows(ExpenseNotFound.class,()-> expenseService.deleteExpense(groupid,expenseid));

        assertEquals("Expense not found with ID: ",ex.getMessage());

    }

    @Test
    void updateExpense_Success() {
        Long expenseId = 1L;
        Long payerId = 10L;
        Long participantId = 20L;

        User payer = new User();
        payer.setUserid(payerId);

        User participant = new User();
        participant.setUserid(participantId);

        ExpenseGroup group = new ExpenseGroup();
        group.setGroupid(100L);

        Expense existingExpense = new Expense();
        existingExpense.setExpenseid(expenseId);
        existingExpense.setGroupid(group);

        AddExpenseRequest request = new AddExpenseRequest();
        request.setAmount(200.0);
        request.setDescription("Dinner");
        request.setPaidBy(payerId);
        request.setParticipants(List.of(participantId));
        request.setSplitType(SplitType.EQUAL);
        request.setValues(List.of(100.0));

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(existingExpense));
        when(userRepository.findById(payerId)).thenReturn(Optional.of(payer));
        when(userRepository.findById(participantId)).thenReturn(Optional.of(participant));
        when(groupMemberRepository.existsById_GroupidAndId_Userid(group.getGroupid(), participantId)).thenReturn(true);
        when(splitStrategyFactory.getStrategy(SplitType.EQUAL)).thenReturn(splitStrategy);
        when(splitStrategy.calculateSplits(any(), eq(payerId), anyList(), anyList()))
                .thenReturn(List.of(new ExpenseSplit()));

        when(expenseRepository.save(any())).thenAnswer(i -> i.getArgument(0));


        Expense updated = expenseService.updateExpense(expenseId, request);


        assertNotNull(updated);
        assertEquals("Dinner", updated.getDescription());
        assertEquals(200.0, updated.getAmount());
        assertEquals(payer, updated.getUserid());

        verify(splitRepository).deleteByExpenseid(expenseId);
        verify(splitRepository).saveAll(anyList());
    }

    @Test
    void updateExpense_ExpenseNotFound() {
        when(expenseRepository.findById(1L)).thenReturn(Optional.empty());

        AddExpenseRequest request = new AddExpenseRequest();
        Exception ex = assertThrows(ExpenseNotFound.class, () -> expenseService.updateExpense(1L, request));

        assertEquals("Expense not found",ex.getMessage());

    }

    @Test
    void updateExpense_PayerNotFound() {
        Long expenseId = 1L;
        Expense expense = new Expense();
        expense.setGroupid(new ExpenseGroup());

        AddExpenseRequest request = new AddExpenseRequest();
        request.setPaidBy(10L);

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));
        when(userRepository.findById(10L)).thenReturn(Optional.empty());

        Exception ex = assertThrows(UserNotFound.class, () -> expenseService.updateExpense(expenseId, request));

        assertEquals("User not found",ex.getMessage());

    }

    @Test
    void updateExpense_ParticipantNotFound() {
        Long expenseId = 1L;
        Long payerId = 10L;
        Long participantId = 20L;

        User payer = new User();
        payer.setUserid(payerId);

        ExpenseGroup group = new ExpenseGroup();
        group.setGroupid(100L);

        Expense expense = new Expense();
        expense.setGroupid(group);

        AddExpenseRequest request = new AddExpenseRequest();
        request.setPaidBy(payerId);
        request.setParticipants(List.of(participantId));

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));
        when(userRepository.findById(payerId)).thenReturn(Optional.of(payer));
        when(userRepository.findById(participantId)).thenReturn(Optional.empty());

       Exception ex = assertThrows(UserNotFound.class, () -> expenseService.updateExpense(expenseId, request));

       assertEquals("Participant with ID " + participantId + " not found",ex.getMessage());

    }

    @Test
    void updateExpense_ParticipantNotInGroup() {
        Long expenseId = 1L;
        Long payerId = 10L;
        Long participantId = 20L;

        User payer = new User();
        payer.setUserid(payerId);

        User participant = new User();
        participant.setUserid(participantId);

        ExpenseGroup group = new ExpenseGroup();
        group.setGroupid(100L);

        Expense expense = new Expense();
        expense.setGroupid(group);

        AddExpenseRequest request = new AddExpenseRequest();
        request.setPaidBy(payerId);
        request.setParticipants(List.of(participantId));

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));
        when(userRepository.findById(payerId)).thenReturn(Optional.of(payer));
        when(userRepository.findById(participantId)).thenReturn(Optional.of(participant));
        when(groupMemberRepository.existsById_GroupidAndId_Userid(group.getGroupid(), participantId)).thenReturn(false);

        Exception ex = assertThrows(UserNotFound.class, () -> expenseService.updateExpense(expenseId, request));

        assertEquals("User ID 20 is not a member of the group", ex.getMessage());

    }

    @Test
    void getExpenseByGroupAndId_Success() {
        Long expenseId = 1L;
        Long groupId = 100L;

        ExpenseGroup group = new ExpenseGroup();
        group.setGroupid(groupId);

        Expense expense = new Expense();
        expense.setExpenseid(expenseId);
        expense.setGroupid(group);

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));

        Expense result = expenseService.getExpenseByGroupAndId(groupId, expenseId);

        assertNotNull(result);
        assertEquals(expenseId, result.getExpenseid());
        assertEquals(groupId, result.getGroupid().getGroupid());
    }

    @Test
    void getExpenseByGroupAndId_ExpenseNotFound() {
        Long expenseId = 2L;
        Long groupId = 100L;

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.empty());

        ExpenseNotFound exception = assertThrows(ExpenseNotFound.class, () ->
                expenseService.getExpenseByGroupAndId(groupId, expenseId)
        );

        assertEquals("Expense not found", exception.getMessage());
    }

    @Test
    void getExpenseByGroupAndId_GroupMismatch_ThrowsException() {
        Long expenseId = 3L;
        Long expectedGroupId = 100L;
        Long actualGroupId = 200L;

        ExpenseGroup actualGroup = new ExpenseGroup();
        actualGroup.setGroupid(actualGroupId);

        Expense expense = new Expense();
        expense.setExpenseid(expenseId);
        expense.setGroupid(actualGroup);

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));

        GroupNotFound exception = assertThrows(GroupNotFound.class, () ->
                expenseService.getExpenseByGroupAndId(expectedGroupId, expenseId)
        );

        assertEquals("Expense does not belong to the specified group", exception.getMessage());

    }

    @Test
    void getExpenseSplitsByGroupAndExpenseId_Success() {
        Long expenseId = 1L;
        Long groupId = 10L;
        Long userId = 100L;

        ExpenseGroup group = new ExpenseGroup();
        group.setGroupid(groupId);

        Expense expense = new Expense();
        expense.setExpenseid(expenseId);
        expense.setGroupid(group);

        ExpenseSplit split = new ExpenseSplit();
        split.setExpenseid(expenseId);
        split.setUserid(userId);
        split.setAmount(500.0);

        User user = new User();
        user.setUserid(userId);
        user.setUsername("aarush");

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));
        when(splitRepository.findByExpenseid(expenseId)).thenReturn(List.of(split));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        List<ExpenseSplitResponse> response = expenseService.getExpenseSplitsByGroupAndExpenseId(groupId, expenseId);

        assertEquals(1, response.size());
        assertEquals("aarush", response.get(0).getUsername());
        assertEquals(500.0, response.get(0).getAmount());
    }


    @Test
    void getExpenseSplitsByGroupAndExpenseId_ExpenseNotFound() {
        when(expenseRepository.findById(1L)).thenReturn(Optional.empty());

       Exception ex = assertThrows(ExpenseNotFound.class, () -> expenseService.getExpenseSplitsByGroupAndExpenseId(10L, 1L));

       assertEquals("Expense not found",ex.getMessage());

    }


    @Test
    void getExpenseSplitsByGroupAndExpenseId_GroupMismatch() {
        Long expenseId = 1L;
        Long correctGroupId = 10L;
        Long wrongGroupId = 99L;

        ExpenseGroup group = new ExpenseGroup();
        group.setGroupid(correctGroupId);

        Expense expense = new Expense();
        expense.setExpenseid(expenseId);
        expense.setGroupid(group);

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));

        Exception ex = assertThrows(GroupNotFound.class, () -> expenseService.getExpenseSplitsByGroupAndExpenseId(wrongGroupId, expenseId));

        assertEquals("Expense does not belong to the specified group",ex.getMessage());

    }

    @Test
    void getExpenseSplitsByGroupAndExpenseId_UserNotFoundInSplit_Throws() {
        Long expenseId = 1L;
        Long groupId = 10L;
        Long userId = 100L;

        ExpenseGroup group = new ExpenseGroup();
        group.setGroupid(groupId);

        Expense expense = new Expense();
        expense.setExpenseid(expenseId);
        expense.setGroupid(group);

        ExpenseSplit split = new ExpenseSplit();
        split.setExpenseid(expenseId);
        split.setUserid(userId);
        split.setAmount(300.0);

        when(expenseRepository.findById(expenseId)).thenReturn(Optional.of(expense));
        when(splitRepository.findByExpenseid(expenseId)).thenReturn(List.of(split));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Exception ex = assertThrows(UserNotFound.class, () -> expenseService.getExpenseSplitsByGroupAndExpenseId(groupId, expenseId));

        assertEquals("User with ID 100 not found",ex.getMessage());

    }








}
