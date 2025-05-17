package com.keerthivasan.crud.Controller;

import com.keerthivasan.crud.Dto.AuthenticationRequest;
import com.keerthivasan.crud.Dto.AuthenticationResponse;
import com.keerthivasan.crud.Model.Role;
import com.keerthivasan.crud.Model.User;
import com.keerthivasan.crud.Repository.UserRepository;
import com.keerthivasan.crud.Security.JwtUtil;
import com.keerthivasan.crud.Service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthenticationRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        try {
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setEmail(request.getEmail());

            // Parse and set the role
            try {
                Role parsedRole = Role.valueOf(request.getRole().toUpperCase());
                user.setRole(parsedRole);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Invalid role. Allowed values: ADMIN, STUDENT");
            }

            userRepository.save(user);
            return ResponseEntity.ok("User registered successfully");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthToken(@RequestBody AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
            final String jwt = jwtUtil.generateToken(userDetails);

            return ResponseEntity.ok(new AuthenticationResponse(jwt));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(403).body("Incorrect username or password");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Authentication failed: " + e.getMessage());
        }
    }

    @GetMapping("/home")
    public ResponseEntity<String> welcome() {
        return ResponseEntity.ok("Welcome! You're authenticated!");
    }
}
