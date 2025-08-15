package splitwise.splitwise.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import splitwise.splitwise.dto.GroupCreateRequest;
import splitwise.splitwise.dto.GroupDetailsResponse;
import splitwise.splitwise.model.ExpenseGroup;
import splitwise.splitwise.service.GroupService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/group")
public class GroupController {

    private final GroupService groupService;

    // post groups
    @PostMapping
    public String createGroup(@RequestBody GroupCreateRequest request){
        groupService.createGroup(request.getGroupname(), request.getUserIds());
        return "Group created Successfully";
    }

    //get group by id
    @GetMapping("/{groupid}")
    public GroupDetailsResponse getGroup(@PathVariable Long groupid){
        return groupService.getGroup(groupid);
    }

    //put  add user to the existing group
    @PutMapping("/{groupid}/users/{userId}")
    public ResponseEntity<String> addUserToGroup(
            @PathVariable Long groupid,@PathVariable Long userId){
        groupService.addUserToGroup(groupid,userId);
        return ResponseEntity.ok("User added to group");
    }

    // remove existing user from group
    @DeleteMapping("/{groupid}/users/{userId}")
    public String removeUser(@PathVariable Long groupid,@PathVariable Long userId){
        groupService.removeUserFromGroup(groupid,userId);
        return  "User removed from group successfully";
    }

    // get groups by user ids
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<ExpenseGroup>> getGroupByUser(@PathVariable Long userId){
        List<ExpenseGroup> groups = groupService.getGroupByUserId(userId);
        return ResponseEntity.ok(groups);
    }

    // delete group
    @DeleteMapping("/{groupid}")
    public ResponseEntity<String> deleteGroup(@PathVariable Long groupid) {
        groupService.deleteGroup(groupid);
        return ResponseEntity.ok("Group deleted successfully");
    }


}
