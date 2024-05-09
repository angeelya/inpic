package angeelya.inPic.user.controller;

import angeelya.inPic.dto.request.UserInformationRequest;
import angeelya.inPic.dto.response.UserDataForProfileResponse;
import angeelya.inPic.exception_handling.exception.DatabaseNotFoundException;
import angeelya.inPic.user.service.UserProfileService;
import angeelya.inPic.exception_handling.exception.ValidationErrorsException;
import angeelya.inPic.validation.service.ValidationErrorsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/profile")
@RequiredArgsConstructor

public class UserProfileController {
    private final ValidationErrorsService validationErrorsService;
    private final UserProfileService userProfileService;
    @PostMapping("/data")
    public ResponseEntity<UserDataForProfileResponse>getUserProfileData(@RequestBody @Valid UserInformationRequest userInformationRequest, BindingResult bindingResult) throws ValidationErrorsException, DatabaseNotFoundException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(userProfileService.getUserData(userInformationRequest));
    }
}
