package angeelya.inPic.auth.controller;

import angeelya.inPic.auth.dto.JwtTokenResponse;
import angeelya.inPic.auth.dto.LogInRequest;
import angeelya.inPic.auth.dto.RegisterRequest;
import angeelya.inPic.auth.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    @PostMapping("/register")
    public ResponseEntity<JwtTokenResponse> register(@RequestBody RegisterRequest registerRequest)
    {
        return ResponseEntity.ok(authenticationService.register(registerRequest));
    }
    @PostMapping("/login")
    public ResponseEntity<JwtTokenResponse> login(@RequestBody LogInRequest logInRequest)
    {
        return ResponseEntity.ok(authenticationService.logIn(logInRequest));
    }
}
