package angeelya.inPic.auth.controller;

import angeelya.inPic.auth.dto.response.JwtTokenResponse;
import angeelya.inPic.auth.dto.request.LogInRequest;
import angeelya.inPic.auth.dto.response.MessageResponse;
import angeelya.inPic.auth.dto.request.RegisterRequest;
import angeelya.inPic.auth.exception.AuthException;
import angeelya.inPic.auth.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegisterRequest registerRequest, BindingResult bindingResult) throws AuthException {
        if (bindingResult.hasErrors()) {
            throw new AuthException(bindingResult.getAllErrors().stream()
                    .map(objectError -> objectError.getDefaultMessage())
                    .collect(Collectors.joining(". ")));
        } else
            return ResponseEntity.ok(authenticationService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtTokenResponse> login(@RequestBody LogInRequest logInRequest) throws AuthException {
        return ResponseEntity.ok(authenticationService.logIn(logInRequest));
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity handlerAuthException(AuthException e) {
        return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
    }
}
