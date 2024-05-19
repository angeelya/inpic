package angeelya.inPic.notification.controller;

import angeelya.inPic.dto.request.UserInformationRequest;
import angeelya.inPic.dto.response.AdminNotificationResponse;
import angeelya.inPic.dto.response.CheckNotificationResponse;
import angeelya.inPic.dto.response.MessageResponse;
import angeelya.inPic.exception_handling.exception.FileException;
import angeelya.inPic.exception_handling.exception.NoAddDatabaseException;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
import angeelya.inPic.exception_handling.exception.ValidationErrorsException;
import angeelya.inPic.notification.service.AdminNotificationService;
import angeelya.inPic.validation.service.ValidationErrorsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notification/admin")
@RequiredArgsConstructor
public class AdminNotificationController {
    private final AdminNotificationService adminNotificationService;
    private final ValidationErrorsService validationErrorsService;
    @PostMapping("/by/user")
    public ResponseEntity<List<AdminNotificationResponse>> getCommentNotification(@RequestBody @Valid UserInformationRequest userInformationRequest, BindingResult bindingResult) throws ValidationErrorsException, NotFoundDatabaseException, FileException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(adminNotificationService.getNotification(userInformationRequest));
    }
    @PostMapping("/check")
    public ResponseEntity<CheckNotificationResponse> checkCommentNotification(@RequestBody @Valid UserInformationRequest userInformationRequest, BindingResult bindingResult) throws ValidationErrorsException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(adminNotificationService.checkNotification(userInformationRequest));
    }
    @PostMapping("/comment/read")
    public ResponseEntity<MessageResponse> updateCommentNotificationRead(@RequestBody @Valid UserInformationRequest userInformationRequest, BindingResult bindingResult) throws ValidationErrorsException, NotFoundDatabaseException, NoAddDatabaseException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(new MessageResponse(adminNotificationService.readNotification(userInformationRequest)));
    }
}
