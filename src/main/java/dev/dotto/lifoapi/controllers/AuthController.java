package dev.dotto.lifoapi.controllers;

import dev.dotto.lifoapi.models.GameRole;
import dev.dotto.lifoapi.models.Role;
import dev.dotto.lifoapi.models.User;
import dev.dotto.lifoapi.repositories.RoleRepository;
import dev.dotto.lifoapi.repositories.UserRepository;
import dev.dotto.lifoapi.security.jwt.JwtUtils;
import dev.dotto.lifoapi.security.request.LoginRequest;
import dev.dotto.lifoapi.security.request.SignupRequest;
import dev.dotto.lifoapi.security.response.MessageResponse;
import dev.dotto.lifoapi.security.response.UserInfoResponse;
import dev.dotto.lifoapi.security.services.UserDetailsImpl;
import dev.dotto.lifoapi.util.AuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Tag(name = "Authentication", description = "APIs for user authentication and registration")
    @Operation(summary = "Sign In", description = "API to authenticate user and set JWT cookie")
    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody LoginRequest loginRequest) {

        // Try to authenticate the user
        Authentication authentication;
        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (AuthenticationException exception) {
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad credentials");
            map.put("status", false);
            return new ResponseEntity<Object>(map, HttpStatus.UNAUTHORIZED);

        }

        // Set the authentication in the security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Get user details from authentication
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Generate JWT cookie
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        // Extract roles from user details
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        // Create user info response
        UserInfoResponse response = new UserInfoResponse(
                userDetails.getId(),
                userDetails.getUsername(),
                roles
        );

        // Return response with JWT cookie
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(response);
    }

    @Tag(name = "Authentication", description = "APIs for user authentication and registration")
    @Operation(summary = "Sign Up", description = "API to register a new user")
    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> signUp(@Valid @RequestBody SignupRequest signupRequest) {

        // Check if username already exists
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        // Check if email already exists
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already taken!"));
        }

        // Create new user account
        User user = new User(
                signupRequest.getUsername(),
                passwordEncoder.encode(signupRequest.getPassword()),
                signupRequest.getEmail()
        );

        // Depecrated: Get roles from request
//        Set<String> strRoles = signupRequest.getRoles();
//        Set<Role> roles = new HashSet<>();
//
//        if (strRoles == null) {
//            Role userRole = roleRepository.findByRoleName(GameRole.USER)
//                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//            roles.add(userRole);
//        } else {
//            strRoles.forEach(role -> {
//                switch (role) {
//                    case "admin":
//                        Role adminRole = roleRepository.findByRoleName(GameRole.ADMIN)
//                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//                        roles.add(adminRole);
//                        break;
//                    default:
//                        Role userRole = roleRepository.findByRoleName(GameRole.USER )
//                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//                        roles.add(userRole);
//                }
//            });
//        }

        // New: Default to USER role
        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByRoleName(GameRole.USER)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(userRole);

        // Set roles to user and save
        user.setRoles(roles);
        userRepository.save(user);

        // Return success response
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new MessageResponse("User registered successfully!"));
    }

    @Tag(name = "Authentication", description = "APIs for user authentication and registration")
    @Operation(summary = "Sign Out", description = "API to sign out the user and clear JWT cookie")
    @PostMapping("/signout")
    public ResponseEntity<MessageResponse> signOut() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponse("Has cerrado sesión correctamente!"));
    }
}