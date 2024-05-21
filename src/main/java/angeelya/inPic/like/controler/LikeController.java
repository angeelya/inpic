package angeelya.inPic.like.controler;

import angeelya.inPic.dto.request.LikeRequest;
import angeelya.inPic.dto.response.MessageResponse;
import angeelya.inPic.exception_handling.exception.*;
import angeelya.inPic.like.service.LikeService;
import angeelya.inPic.validation.service.ValidationErrorsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/like")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;
    private final ValidationErrorsService validationErrorsService;
    @PostMapping("/add")
    public ResponseEntity<MessageResponse>like(@RequestBody @Valid LikeRequest likeRequest, BindingResult bindingResult) throws ValidationErrorsException, NotFoundDatabaseException, NoAddDatabaseException, ExistException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(new MessageResponse(likeService.addLike(likeRequest)));
    }
    @PostMapping("/delete")
    public ResponseEntity<MessageResponse> delete(@RequestBody @Valid LikeRequest likeRequest, BindingResult bindingResult) throws ValidationErrorsException, NotFoundDatabaseException, DeleteDatabaseException, NoAddDatabaseException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(new MessageResponse(likeService.deleteLike(likeRequest)));
    }
}
