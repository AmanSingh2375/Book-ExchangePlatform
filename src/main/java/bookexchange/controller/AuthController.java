package bookexchange.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bookexchange.model.User;
import bookexchange.repository.UserRepository;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (user.getName() == null || user.getName().isBlank()
                || user.getEmail() == null || user.getEmail().isBlank()
                || user.getPassword() == null || user.getPassword().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "All fields are required"));
        }

        String email = user.getEmail().trim().toLowerCase();

        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Email is already registered"));
        }

        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Registration successful",
                "id", savedUser.getId(),
                "name", savedUser.getName(),
                "email", savedUser.getEmail()
        ));
    }

   @PostMapping("/login")
public ResponseEntity<?> login(
        @RequestBody User user,
        HttpSession session) {

    if (user.getEmail() == null || user.getPassword() == null) {
        return ResponseEntity.badRequest()
                .body(Map.of("message", "Email and password are required"));
    }

    String email = user.getEmail().trim().toLowerCase();

    User existingUser = userRepository.findByEmail(email).orElse(null);

    if (existingUser == null ||
            !passwordEncoder.matches(
                    user.getPassword(), existingUser.getPassword())) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Invalid email or password"));
    }

    session.setAttribute("userId", existingUser.getId());
    session.setAttribute("userName", existingUser.getName());

    return ResponseEntity.ok(Map.of(
            "message", "Login successful",
            "id", existingUser.getId(),
            "name", existingUser.getName(),
            "email", existingUser.getEmail()
    ));
}
@GetMapping("/me")
public ResponseEntity<?> currentUser(HttpSession session) {
    Object userId = session.getAttribute("userId");
    Object userName = session.getAttribute("userName");

    if (userId == null || userName == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Not logged in"));
    }

    return ResponseEntity.ok(Map.of(
            "id", userId,
            "name", userName
    ));
}
@PostMapping("/logout")
public ResponseEntity<?> logout(HttpSession session) {
    session.invalidate();
    return ResponseEntity.ok(
            Map.of("message", "Logout successful")
    );
}
}