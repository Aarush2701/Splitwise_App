package splitwise.splitwise.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import splitwise.splitwise.dto.AddExpenseRequest;
import splitwise.splitwise.dto.ExpenseSplitResponse;
import splitwise.splitwise.model.Expense;
import splitwise.splitwise.service.ExpenseService;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/group/{groupid}/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    // Add expense and split equally

    @PostMapping
    public ResponseEntity<Expense> addExpense(
            @PathVariable Long groupid,
            @RequestBody AddExpenseRequest request
            ) {
        Expense expense = expenseService.addExpense(groupid, request);
        return ResponseEntity.ok(expense);
    }

     // → Get all expenses in a group

    @GetMapping
    public ResponseEntity<List<Expense>> getGroupExpenses(@PathVariable Long groupid) {
        return ResponseEntity.ok(expenseService.getExpensesByGroup(groupid));
    }

     //Get all expenses by a specific user in a group

    @GetMapping("/users/{userid}")
    public ResponseEntity<List<Expense>> getUserExpensesInGroup(
            @PathVariable Long groupid,
            @PathVariable Long userid
    ) {
        return ResponseEntity.ok(expenseService.getExpensesByGroupAndUser(groupid, userid));
    }

    // expense delete
    @DeleteMapping("/{expenseid}")
    public ResponseEntity<?> deleteExpense(
            @PathVariable("groupid") Long groupid,
            @PathVariable("expenseid") Long expenseid
    ) {
        expenseService.deleteExpense(groupid, expenseid);
        return ResponseEntity.ok(Collections.singletonMap("message", "Expense deleted successfully"));
    }

    //edir expense
    @PutMapping("/{expenseid}")
    public ResponseEntity<Expense> updateExpense(
            @PathVariable Long expenseid,
            @RequestBody AddExpenseRequest request) {
        Expense updated = expenseService.updateExpense(expenseid, request);
        return ResponseEntity.ok(updated);
    }

    // expense by groupid and user id
    @GetMapping("/{expenseid}")
    public ResponseEntity<Expense> getExpenseByGroupAndId(@PathVariable Long groupid, @PathVariable Long expenseid) {
        Expense expense = expenseService.getExpenseByGroupAndId(groupid, expenseid);
        return ResponseEntity.ok(expense);
    }
    // splits of expense
    @GetMapping("/{expenseid}/splits")
    public ResponseEntity<List<ExpenseSplitResponse>> getExpenseSplits(
            @PathVariable Long groupid,
            @PathVariable Long expenseid) {
        List<ExpenseSplitResponse> splits = expenseService.getExpenseSplitsByGroupAndExpenseId(groupid, expenseid);
        return ResponseEntity.ok(splits);
    }

}
