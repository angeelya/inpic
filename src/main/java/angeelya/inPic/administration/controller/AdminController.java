package angeelya.inPic.administration.controller;

import angeelya.inPic.administration.service.AdminService;
import angeelya.inPic.dto.request.ImageRequest;
import angeelya.inPic.dto.response.ImageResponse;
import angeelya.inPic.dto.response.MessageResponse;
import angeelya.inPic.exception_handling.exception.*;
import angeelya.inPic.validation.service.ValidationErrorsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final ValidationErrorsService validationErrorsService;
    @GetMapping("/all")
    public ResponseEntity<List<ImageResponse>> getAllImagePosts() throws NotFoundDatabaseException, FileException {
        return ResponseEntity.ok(adminService.getAllImagePost());
    }
    @PostMapping("/delete/image/post")
    public ResponseEntity<MessageResponse> deleteImagePost(@RequestBody @Valid ImageRequest imageRequest, BindingResult bindingResult) throws ValidationErrorsException, NotFoundDatabaseException, DeleteDatabaseException, NoAddDatabaseException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(new MessageResponse(adminService.deleteImagePost(imageRequest)));
    }
}
