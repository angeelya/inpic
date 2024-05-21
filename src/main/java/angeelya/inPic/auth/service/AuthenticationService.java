package angeelya.inPic.auth.service;

import angeelya.inPic.dto.response.JwtTokenResponse;
import angeelya.inPic.dto.request.LogInRequest;
import angeelya.inPic.dto.request.RegisterRequest;
import angeelya.inPic.exception_handling.exception.AuthException;
import angeelya.inPic.database.repository.UserRepository;
import angeelya.inPic.database.model.Role;
import angeelya.inPic.database.model.User;
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
    private static final String MS_INVALID_DATA = "Invalid username or password";


    public JwtTokenResponse register(RegisterRequest request) {
        var user = User.builder().login(request.getLogin())
                .email(request.getEmail()).password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER).build();
        userRepository.save(user);
        var jwt = jwtService.generateToken(user);
        return JwtTokenResponse.builder().user_id(user.getId()).jwtToken(jwt).role(user.getRole().name()).build();
    }

    public JwtTokenResponse logIn(LogInRequest request) throws AuthException {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getLogin(), request.getPassword()));
            User user=userRepository.findByLogin(request.getLogin()).get();
            var jwt = jwtService.generateToken(user);
            return JwtTokenResponse.builder().jwtToken(jwt).user_id(user.getId()).role(user.getRole().name()).build();
        } catch (AuthenticationException e) {
            logger.info(e.getMessage());
            throw new AuthException(MS_INVALID_DATA);
        }
    }
}
