package angeelya.inPic.user.controller;

import angeelya.inPic.dto.request.*;
import angeelya.inPic.dto.response.MessageResponse;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
import angeelya.inPic.exception_handling.exception.NoAddDatabaseException;
import angeelya.inPic.exception_handling.exception.PasswordUpdateException;
import angeelya.inPic.exception_handling.exception.ValidationErrorsException;
import angeelya.inPic.user.service.UserSettingsService;
import angeelya.inPic.validation.service.ValidationErrorsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/settings")
@RequiredArgsConstructor
public class UserSettingsController {
    private final UserSettingsService userSettingsService;
    private final ValidationErrorsService validationErrorsService;

    @PostMapping("/update/email")
    public ResponseEntity<MessageResponse> updateEmail(@RequestBody @Valid EmailUpdateRequest updateRequest, BindingResult bindingResult) throws ValidationErrorsException, NotFoundDatabaseException, NoAddDatabaseException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(new MessageResponse(userSettingsService.updateEmail(updateRequest)));
    }

    @PostMapping("/update/name")
    public ResponseEntity<MessageResponse> updateName(@RequestBody @Valid NameUpdateRequest updateRequest, BindingResult bindingResult) throws ValidationErrorsException, NotFoundDatabaseException, NoAddDatabaseException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(new MessageResponse(userSettingsService.updateName(updateRequest)));
    }

    @PostMapping("/update/description")
    public ResponseEntity<MessageResponse> updateDescription(@RequestBody @Valid DescriptionUpdateRequest descriptionUpdateRequest, BindingResult bindingResult) throws ValidationErrorsException, NotFoundDatabaseException, NoAddDatabaseException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(new MessageResponse(userSettingsService.updateDescription(descriptionUpdateRequest)));
    }

    @PostMapping("/update/password")
    public ResponseEntity<MessageResponse> updatePassword(@RequestBody @Valid PasswordUpdateRequest passwordUpdateRequest, BindingResult bindingResult) throws ValidationErrorsException, NotFoundDatabaseException, NoAddDatabaseException, PasswordUpdateException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(new MessageResponse(userSettingsService.updatePassword(passwordUpdateRequest)));
    }

    @PostMapping("/update/image")
    public ResponseEntity<MessageResponse> updateImage(@RequestBody @Valid UserInformationRequest userInformationRequest, BindingResult bindingResult, @RequestParam("file") MultipartFile multipartFile) throws ValidationErrorsException, NotFoundDatabaseException, NoAddDatabaseException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(new MessageResponse(userSettingsService.updateUserImage(multipartFile,userInformationRequest)));
    }
}
