package angeelya.inPic.comment.controller;

import angeelya.inPic.comment.service.CommentService;
import angeelya.inPic.dto.request.CommentAddRequest;
import angeelya.inPic.dto.request.CommentDeleteRequest;
import angeelya.inPic.dto.request.ImageRequest;
import angeelya.inPic.dto.response.CommentResponse;
import angeelya.inPic.dto.response.MessageResponse;
import angeelya.inPic.exception_handling.exception.*;
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
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final ValidationErrorsService validationErrorsService;

    @PostMapping("/get/by/image")
    public ResponseEntity<List<CommentResponse>> getImageComments(@RequestBody @Valid ImageRequest imageRequest, BindingResult bindingResult) throws ValidationErrorsException, NotFoundDatabaseException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(commentService.getImageComments(imageRequest));
    }

    @PostMapping("/add")
    public ResponseEntity<MessageResponse> addComment(@RequestBody @Valid CommentAddRequest commentAddRequest, BindingResult bindingResult) throws ValidationErrorsException, NotFoundDatabaseException, NoAddDatabaseException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(new MessageResponse(commentService.addComment(commentAddRequest)));
    }

    @PostMapping("/delete")
    public ResponseEntity<MessageResponse> deleteComment(@RequestBody @Valid CommentDeleteRequest commentDeleteRequest, BindingResult bindingResult) throws ValidationErrorsException, NotFoundDatabaseException, DeleteDatabaseException, NoAddDatabaseException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(new MessageResponse(commentService.deleteComment(commentDeleteRequest)));
    }

    @PostMapping("/update")
    public ResponseEntity<MessageResponse> updateComment(@RequestBody @Valid CommentAddRequest commentAddRequest, BindingResult bindingResult) throws ValidationErrorsException, NotFoundDatabaseException, NoAddDatabaseException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(new MessageResponse(commentService.updateComment(commentAddRequest)));
    }
}
