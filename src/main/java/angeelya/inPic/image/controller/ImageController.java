package angeelya.inPic.image.controller;

import angeelya.inPic.dto.request.ImageAddRequest;
import angeelya.inPic.dto.request.ImagePageRequest;
import angeelya.inPic.dto.request.UserInformationRequest;
import angeelya.inPic.dto.response.ImagePageResponse;
import angeelya.inPic.dto.response.ImageResponse;
import angeelya.inPic.dto.response.MessageResponse;
import angeelya.inPic.exception_handling.exception.NoAddDatabaseException;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
import angeelya.inPic.exception_handling.exception.FileException;
import angeelya.inPic.exception_handling.exception.ValidationErrorsException;
import angeelya.inPic.validation.service.ValidationErrorsService;
import angeelya.inPic.image.service.ImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/image")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;
    private final ValidationErrorsService validationErrorsService;
    @PostMapping("/is/liked/by/user")
    public ResponseEntity<List<ImageResponse>> getLikedImage(@RequestBody @Valid UserInformationRequest userInformationRequest, BindingResult bindingResult) throws FileException, ValidationErrorsException, NotFoundDatabaseException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(imageService.getLikedImage(userInformationRequest));
    }

    @PostMapping("/page/data")
    public ResponseEntity<ImagePageResponse> getPageImage(@RequestBody @Valid ImagePageRequest imagePageRequest, BindingResult bindingResult) throws FileException, ValidationErrorsException, NotFoundDatabaseException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(imageService.getImageData(imagePageRequest));
    }
    @PostMapping("/add")
    public ResponseEntity<MessageResponse>addImage(@RequestBody @Valid ImageAddRequest imageAddRequest,BindingResult bindingResult, @RequestParam("file")MultipartFile multipartFile) throws ValidationErrorsException, NotFoundDatabaseException, FileException, NoAddDatabaseException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(new MessageResponse(imageService.addImage(imageAddRequest,multipartFile)));
    }
}
