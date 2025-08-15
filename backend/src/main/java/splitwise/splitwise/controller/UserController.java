package splitwise.splitwise.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import splitwise.splitwise.dto.EmailListRequest;
import splitwise.splitwise.dto.UserIdListResponse;
import splitwise.splitwise.dto.UserResponseDTO;
import splitwise.splitwise.dto.UserUpdateRequest;
import splitwise.splitwise.model.User;
import splitwise.splitwise.repository.UserRepository;
import splitwise.splitwise.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    private final UserRepository userRepository;

    // create user
   /* @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user){
        User savedUser = userService.createUser(user);
        return ResponseEntity.ok(savedUser);
    }*/


    //update user
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest updateUser){
        User user = userService.updateUser(id, updateUser);
        return ResponseEntity.ok(user);
    }

    // delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id){
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted Successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id).orElseThrow(()->new RuntimeException("user not found"));
        UserResponseDTO dto = new UserResponseDTO(
                user.getUserid(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone()
        );
        return ResponseEntity.ok(dto);
    }

    //test to check
    @GetMapping("/ping")
    public ResponseEntity<String> servertest(){

    return ResponseEntity.ok("pong");}

    // email to user ids
    @PostMapping("/resolve-ids")
    public ResponseEntity<UserIdListResponse> resolveUserIds(@RequestBody EmailListRequest request) {
        List<Long> userIds = userService.resolveUserIds(request.getEmails());
        return ResponseEntity.ok(new UserIdListResponse(userIds));
    }

}
