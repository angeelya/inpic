package angeelya.inPic.auth.service;

import angeelya.inPic.auth.dto.JwtTokenResponse;
import angeelya.inPic.auth.dto.LogInRequest;
import angeelya.inPic.auth.dto.RegisterRequest;
import angeelya.inPic.auth.repository.UserRepository;
import angeelya.inPic.model.Role;
import angeelya.inPic.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    public JwtTokenResponse register(RegisterRequest request) {
        var user = User.builder().login(request.getLogin())
                .email(request.getEmail()).password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER).build();
        userRepository.save(user);
        var jwt = jwtService.generateToken(user);
        return JwtTokenResponse .builder().jwtToken(jwt).build();
    }

    public JwtTokenResponse  logIn(LogInRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword()));
        var user = userRepository.findByName(request.getLogin())
                .orElseThrow(() -> new IllegalArgumentException("Invalid login or password"));
        var jwt = jwtService.generateToken(user);
        return JwtTokenResponse.builder().jwtToken(jwt).build();
    }
}
