package dev.dotto.lifoapi.controllers;

import dev.dotto.lifoapi.payloads.UserDTO;
import dev.dotto.lifoapi.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @Tag(name = "User Management", description = "APIs for managing users")
    @Operation(summary = "Get logged user details", description = "API to retrieve details of the logged-in user")
    @GetMapping("/users/logged")
    public ResponseEntity<UserDTO> getLoggedUserDetails() {
        UserDTO userDTO = userService.getLoggedUserDetails();
        return ResponseEntity.ok(userDTO);
    }
}
