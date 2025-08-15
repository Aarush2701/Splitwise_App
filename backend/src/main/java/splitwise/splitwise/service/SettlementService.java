package splitwise.splitwise.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

@Service
@RequiredArgsConstructor
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final UserRepository userRepository;
    private final ExpenseGroupRepository groupRepository;
    private final ExpenseService expenseService;

    // create settlement
    public Settlement createSettlement(Long groupid, Long paidbyId, Long paidtoId, Double amount) {
        if (paidbyId.equals(paidtoId)){
            throw new PayerAndPayeeSame("Payer and Payee cannot be the same user");
        }

        User paidby = userRepository.findById(paidbyId)
                .orElseThrow(() -> new UserNotFound("Payer not found"));

        User paidto = userRepository.findById(paidtoId)
                .orElseThrow(() -> new UserNotFound("Payee not found"));

        ExpenseGroup group = groupRepository.findById(groupid)
                .orElseThrow(() -> new GroupNotFound("Group not found"));

        double balance = expenseService.getBalanceBetweenUsers(groupid,paidbyId,paidtoId);
        if (balance <= 0.0){
            throw new NoDuesExist("No dues exist between these users");
        }

        if (amount > balance) {
            throw new SettleAmountMoreThanDue("Settlement amount exceeds dues. Due amount: " + balance);
        }

        Settlement settlement = new Settlement();
        settlement.setGroupid(group);
        settlement.setPaidby(paidby);
        settlement.setPaidto(paidto);
        settlement.setAmount(amount);
        settlement.setDate(Timestamp.valueOf(LocalDateTime.now()));

        return settlementRepository.save(settlement);
    }

    //get settlement in a group by groupid
    public List<Settlement> getSettlementsByGroup(Long groupid) {
        ExpenseGroup group = groupRepository.findById(groupid)
                .orElseThrow(() -> new GroupNotFound("Group not found"));
        return settlementRepository.findByGroupid_Groupid(groupid);
    }

    // get settelements made by a user
    public List<Settlement> getSettlementsPaidByUser(Long groupid, Long userid){
        ExpenseGroup group = groupRepository.findById(groupid)
                .orElseThrow(() -> new GroupNotFound("Group not found"));
        User paidby = userRepository.findById(userid)
                .orElseThrow(() -> new UserNotFound("Payer not found"));

        return settlementRepository.findByGroupid_GroupidAndPaidby_Userid(groupid,userid);
    }

    // get settlement made to a user
    public List<Settlement> getSettlementsPaidToUser(Long groupid, Long userid){
        ExpenseGroup group = groupRepository.findById(groupid)
                .orElseThrow(() -> new GroupNotFound("Group not found"));
        User paidto  = userRepository.findById(userid)
                .orElseThrow(() -> new UserNotFound("Payee not found"));

        return settlementRepository.findByGroupid_GroupidAndPaidto_Userid(groupid,userid);
    }

    public void deleteSettlement(Long settlementid) {
        Settlement settlement = settlementRepository.findById(settlementid)
                .orElseThrow(() -> new SettlementNotFound("Settlement not found"));
        settlementRepository.delete(settlement);
    }

    public Settlement updateSettlement(Long settlementid, UpdateSettlementRequest request) {
        Settlement settlement = settlementRepository.findById(settlementid)
                .orElseThrow(() -> new SettlementNotFound("Settlement not found"));

        User paidBy = userRepository.findById(request.getPaidby())
                .orElseThrow(() -> new UserNotFound("PaidBy user not found"));

        User paidTo = userRepository.findById(request.getPaidto())
                .orElseThrow(() -> new UserNotFound("PaidTo user not found"));

        Long groupid = settlement.getGroupid().getGroupid();

        double netDue = expenseService.getBalanceBetweenUsers(groupid, paidBy.getUserid(), paidTo.getUserid());

        double adjustedDue = netDue + settlement.getAmount();

        if (request.getAmount() > adjustedDue){
            throw new SettleAmountMoreThanDue("Settlement amount exceeds dues. Due amount: " + adjustedDue);
        }

        settlement.setPaidby(paidBy);
        settlement.setPaidto(paidTo);
        settlement.setAmount(request.getAmount());
        settlement.setDate(Timestamp.valueOf(LocalDateTime.now()));

        return settlementRepository.save(settlement);
    }
}
