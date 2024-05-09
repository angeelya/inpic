package angeelya.inPic.auth.controller;

import angeelya.inPic.dto.response.JwtTokenResponse;
import angeelya.inPic.dto.request.LogInRequest;
import angeelya.inPic.dto.response.MessageResponse;
import angeelya.inPic.dto.request.RegisterRequest;
import angeelya.inPic.exception_handling.exception.AuthException;
import angeelya.inPic.auth.service.AuthenticationService;
import angeelya.inPic.exception_handling.exception.ValidationErrorsException;
import angeelya.inPic.validation.service.ValidationErrorsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final ValidationErrorsService validationErrorsService;

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegisterRequest registerRequest, BindingResult bindingResult) throws ValidationErrorsException {
        validationErrorsService.validation(bindingResult);
            return ResponseEntity.ok(authenticationService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtTokenResponse> login(@RequestBody @Valid LogInRequest logInRequest, BindingResult bindingResult) throws AuthException, ValidationErrorsException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(authenticationService.logIn(logInRequest));
    }

}
