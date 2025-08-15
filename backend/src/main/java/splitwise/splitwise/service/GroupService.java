package splitwise.splitwise.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import splitwise.splitwise.dto.GroupDetailsResponse;
import splitwise.splitwise.dto.GroupMemberDTO;
import splitwise.splitwise.exception.*;
import splitwise.splitwise.model.*;
import splitwise.splitwise.repository.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final ExpenseGroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final ExpenseService expenseService;
    private final SettlementRepository settlementRepository;
    private final ExpenseRepository expenseRepository;
    private final ExpenseSplitRepository splitRepository;

    // create group with initial members
    @Transactional
    public ExpenseGroup createGroup(String groupname, List<Long> userIds){
        ExpenseGroup group = new ExpenseGroup();
        group.setGroupname(groupname);
        group = groupRepository.save(group);

        for (Long userid: userIds){
            User user = userRepository.findById(userid)
                    .orElseThrow(() -> new UserNotFound("User not found with id:" + userid));

            GroupMember member = new GroupMember();
            member.setId(new GroupMemberId(group.getGroupid(), userid));
            member.setJoiningdate(new java.sql.Timestamp(System.currentTimeMillis()));
            member.setGroup(group);
            member.setUser(user);
            groupMemberRepository.save(member);
        }

        return group;
    }


    // get group details
    public GroupDetailsResponse getGroup(Long groupid){
        ExpenseGroup group = groupRepository.findById(groupid)
                .orElseThrow(()-> new GroupNotFound("Group not found"));
        List<GroupMember> groupMembers = groupMemberRepository.findById_Groupid(groupid);

        List<GroupMemberDTO> memberDTOS = groupMembers.stream()
                .map(member ->{
                    Long userid = member.getId().getUserid();
                    User user = userRepository.findById(userid)
                            .orElseThrow(() -> new UserNotFound("User not found with ID: " + userid));
                        return new GroupMemberDTO(userid, user.getUsername());
                })
                .toList();

        GroupDetailsResponse response = new GroupDetailsResponse();
        response.setGroupId(group.getGroupid());
        response.setGroupname(group.getGroupname());
        response.setMembers(memberDTOS);

        return response;
    }



    // add user to existing group
    public void addUserToGroup(Long groupid, Long userId){
        boolean exists = groupMemberRepository.existsById_GroupidAndId_Userid(groupid, userId);
        ExpenseGroup group = groupRepository.findById(groupid)
                .orElseThrow(()->new GroupNotFound("Group not found"));
        if (exists) throw new UserAlreadyExists("User already in group");

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new UserNotFound("User not found"));

        GroupMember member = new GroupMember();
        member.setId(new GroupMemberId(groupid, userId));
        member.setJoiningdate(new java.sql.Timestamp(System.currentTimeMillis()));
        member.setUser(user);
        member.setGroup(group);

        groupMemberRepository.save(member);
    }


    @Transactional
    public void removeUserFromGroup(Long groupid, Long userId) {
        ExpenseGroup group = groupRepository.findById(groupid)
                .orElseThrow(() -> new GroupNotFound("Group not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFound("User not found"));

        // Check if user is in the group
        GroupMemberId groupMemberId = new GroupMemberId(groupid, userId);
        if (!groupMemberRepository.existsById(groupMemberId)) {
            throw new UserNotInGroup("User is not part of the group");
        }

        // Check dues
        List<String> balances = expenseService.getBalances(groupid);
        boolean hasDues = balances.stream()
                .anyMatch(balance -> balance.contains(user.getUsername()));
        if (hasDues) {
            throw new UserWithPendingDues("Cannot remove user with unsettled balances.");
        }

        //  Delete settlements where user is a participant in that group
        List<Settlement> settlements = settlementRepository.findByGroupid_Groupid(groupid).stream()
                .filter(s -> s.getPaidby().getUserid().equals(userId) || s.getPaidto().getUserid().equals(userId))
                .toList();
        settlementRepository.deleteAll(settlements);

        //  Delete splits where user is a participant in that group
        List<Expense> groupExpenses = expenseRepository.findByGroupid_Groupid(groupid);
        for (Expense expense : groupExpenses) {
            List<ExpenseSplit> splits = splitRepository.findByExpenseid(expense.getExpenseid()).stream()
                    .filter(split -> split.getUserid().equals(userId))
                    .toList();
            splitRepository.deleteAll(splits);
        }

        //  Delete expenses created by the user in that group
        List<Expense> userExpensesInGroup = groupExpenses.stream()
                .filter(expense -> expense.getUserid().getUserid().equals(userId))
                .toList();

        for (Expense expense : userExpensesInGroup) {
            // delete the splits first linked with each expense
            splitRepository.deleteByExpenseid(expense.getExpenseid());
        }
        expenseRepository.deleteAll(userExpensesInGroup);

        // after splits are removed then remove user from group
        groupMemberRepository.deleteById(groupMemberId);
    }


    public List<ExpenseGroup> getGroupByUserId(Long userId){
        if(!userRepository.existsById(userId)){
            throw new UserNotFound("User not found with ID " +userId);
        }

       List<GroupMember> groupMembers = groupMemberRepository.findById_Userid(userId);

       if (groupMembers.isEmpty()){
           throw new NoGroupFoundException("User with ID " + userId + " is not part of any group");
       }

       return groupMembers.stream()
               .map(GroupMember::getGroup)
               .distinct()
               .collect(Collectors.toList());
    }


    public void deleteGroup(Long groupid) {
        ExpenseGroup group = groupRepository.findById(groupid)
                .orElseThrow(() -> new GroupNotFound("Group not found"));

        // Check for any unsettled balances
        List<String> balances = expenseService.getBalances(groupid);
        boolean hasDues = balances.stream().anyMatch(b -> b.contains("owes"));

        if (hasDues) {
            throw new GroupHasPendingDuesException("Cannot delete group with unsettled balances.");
        }

        // Delete settlements
        List<Settlement> settlements = settlementRepository.findByGroupid_Groupid(groupid);
        settlementRepository.deleteAll(settlements);

        // Delete expenses and splits
        List<Expense> expenses = expenseRepository.findByGroupid_Groupid(groupid);
        for (Expense expense : expenses) {
            splitRepository.deleteByExpenseid(expense.getExpenseid());
        }
        expenseRepository.deleteAll(expenses);

        // Delete group members
        List<GroupMember> members = groupMemberRepository.findById_Groupid(groupid);
        groupMemberRepository.deleteAll(members);

        // Finally delete the group
        groupRepository.deleteById(groupid);
    }

}
