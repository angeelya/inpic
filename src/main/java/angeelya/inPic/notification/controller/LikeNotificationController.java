package angeelya.inPic.notification.controller;

import angeelya.inPic.dto.request.UserInformationRequest;
import angeelya.inPic.dto.response.CheckNotificationResponse;
import angeelya.inPic.dto.response.CommentNotificationResponse;
import angeelya.inPic.dto.response.LikeNotificationResponse;
import angeelya.inPic.dto.response.MessageResponse;
import angeelya.inPic.exception_handling.exception.FileException;
import angeelya.inPic.exception_handling.exception.NoAddDatabaseException;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
import angeelya.inPic.exception_handling.exception.ValidationErrorsException;
import angeelya.inPic.notification.service.CommentNotificationService;
import angeelya.inPic.notification.service.LikeNotificationService;
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
@RequestMapping("/notification/like")
@RequiredArgsConstructor
public class LikeNotificationController {
    private final LikeNotificationService likeNotificationService;
    private final ValidationErrorsService validationErrorsService;

    @PostMapping("by/user")
    public ResponseEntity<List<LikeNotificationResponse>> getLikeNotification(@RequestBody @Valid UserInformationRequest userInformationRequest, BindingResult bindingResult) throws ValidationErrorsException, NotFoundDatabaseException, FileException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(likeNotificationService.getNotification(userInformationRequest));
    }
    @PostMapping("/check")
    public ResponseEntity<CheckNotificationResponse> checkLikeNotification(@RequestBody @Valid UserInformationRequest userInformationRequest, BindingResult bindingResult) throws ValidationErrorsException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(likeNotificationService.checkNotification(userInformationRequest));
    }
    @PostMapping("/read")
    public ResponseEntity<MessageResponse> updateLikeNotificationRead(@RequestBody @Valid UserInformationRequest userInformationRequest, BindingResult bindingResult) throws ValidationErrorsException, NotFoundDatabaseException, NoAddDatabaseException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(new MessageResponse(likeNotificationService.readNotification(userInformationRequest)));
    }
}
