package dev.dotto.lifoapi.controllers;

import dev.dotto.lifoapi.models.User;
import dev.dotto.lifoapi.models.UserItem;
import dev.dotto.lifoapi.payloads.UserItemDTO;
import dev.dotto.lifoapi.payloads.UserItemResponse;
import dev.dotto.lifoapi.payloads.user.UserDTO;
import dev.dotto.lifoapi.services.UserService;
import dev.dotto.lifoapi.util.AuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private UserService userService;

    @Tag(name = "User Management", description = "APIs for managing users")
    @Operation(summary = "Get logged user details", description = "API to retrieve details of the logged-in user")
    @GetMapping("/users/logged")
    public ResponseEntity<UserDTO> getLoggedUserDetails() {
        Long userId = authUtil.loggedInUserId();
        UserDTO userDTO = userService.getUserDetails(userId);
        return ResponseEntity.ok(userDTO);
    }

    @Tag(name = "User Management", description = "APIs for managing users")
    @Operation(summary = "Get logged user items", description = "API to retrieve items associated with the logged-in user")
    @GetMapping("/users/logged/items")
    public ResponseEntity<UserItemResponse> getLoggedUserItems() {
        Long userId = authUtil.loggedInUserId();
        UserItemResponse userItems = userService.getUserItems(userId);
        return ResponseEntity.ok(userItems);
    }
}
