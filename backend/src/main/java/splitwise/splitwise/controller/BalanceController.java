package splitwise.splitwise.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import splitwise.splitwise.service.ExpenseService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/group/{groupid}/balances")
public class BalanceController {

    private final ExpenseService expenseService;

    // get balance by groupid
    @GetMapping
    public ResponseEntity<List<String>> getGroupBalances(@PathVariable Long groupid){
        return ResponseEntity.ok(expenseService.getBalances(groupid));
    }

    // get balance of a particular user in a group
    @GetMapping("/users/{userid}")
    public ResponseEntity<List<String>> getUserBalanceInGroups(@PathVariable Long groupid, @PathVariable Long userid){
        return ResponseEntity.ok(expenseService.getUserBalance(groupid, userid));
    }

}
