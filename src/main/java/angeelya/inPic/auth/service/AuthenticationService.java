package angeelya.inPic.auth.service;

import angeelya.inPic.auth.dto.response.JwtTokenResponse;
import angeelya.inPic.auth.dto.request.LogInRequest;
import angeelya.inPic.auth.dto.request.RegisterRequest;
import angeelya.inPic.auth.exception.AuthException;
import angeelya.inPic.auth.repository.UserRepository;
import angeelya.inPic.model.Role;
import angeelya.inPic.model.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    public JwtTokenResponse register(RegisterRequest request) {
        var user = User.builder().login(request.getLogin())
                .email(request.getEmail()).password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER).build();
        userRepository.save(user);
        var jwt = jwtService.generateToken(user);
        return JwtTokenResponse.builder().jwtToken(jwt).build();
    }

    public JwtTokenResponse logIn(LogInRequest request) throws AuthException {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword()));
            var jwt = jwtService.generateToken(userRepository.findByLogin(request.getLogin()).get());
            return JwtTokenResponse.builder().jwtToken(jwt).build();
        } catch (AuthenticationException e) {
            logger.info(e.getMessage());
            throw new AuthException("Invalid username or password");
        }
    }
}
