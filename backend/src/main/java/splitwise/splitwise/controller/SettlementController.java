package splitwise.splitwise.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import splitwise.splitwise.dto.CreateSettlementRequest;
import splitwise.splitwise.dto.UpdateSettlementRequest;
import splitwise.splitwise.model.Settlement;
import splitwise.splitwise.service.SettlementService;

import java.util.List;

@RestController
@RequestMapping("/group/{groupid}/settlements")
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementService settlementService;

    // posting settlement details
    @PostMapping
    public ResponseEntity<Settlement> createSettlement(
            @PathVariable Long groupid,
            @RequestBody CreateSettlementRequest request
            ) {
        Settlement settlement = settlementService.createSettlement(
                groupid,
                request.getPaidby(),
                request.getPaidto(),
                request.getAmount()
        );
        return ResponseEntity.ok(settlement);
    }

    // getting settlement details
    @GetMapping
    public ResponseEntity<List<Settlement>> getSettlements(@PathVariable Long groupid) {
        return ResponseEntity.ok(settlementService.getSettlementsByGroup(groupid));
    }

    // get settlements made by an user in a group
    @GetMapping("/paidby/{userid}")
    public List<Settlement> getSettlementByPayer(@PathVariable Long groupid, @PathVariable Long userid){
        return settlementService.getSettlementsPaidByUser(groupid, userid);
    }

    // get settlements made to an user in a group
    @GetMapping("/paidto/{userid}")
    public List<Settlement> getSettlementByReceiver(@PathVariable Long groupid, @PathVariable Long userid){
        return settlementService.getSettlementsPaidToUser(groupid, userid);
    }

    // delete settlement
    @DeleteMapping("/{settlementid}")
    public ResponseEntity<String> deleteSettlement(@PathVariable Long settlementid) {
        settlementService.deleteSettlement(settlementid);
        return ResponseEntity.ok("Settlement deleted successfully");
    }

    // update settlement
    @PutMapping("/{settlementid}")
    public ResponseEntity<Settlement> updateSettlement(
            @PathVariable Long settlementid,
            @RequestBody UpdateSettlementRequest request) {
        Settlement updated = settlementService.updateSettlement(settlementid, request);
        return ResponseEntity.ok(updated);
    }
}
