package angeelya.inPic.image.controller;

import angeelya.inPic.dto.request.*;
import angeelya.inPic.dto.response.ImagePageResponse;
import angeelya.inPic.dto.response.ImageResponse;
import angeelya.inPic.dto.response.MessageResponse;
import angeelya.inPic.exception_handling.exception.*;
import angeelya.inPic.image.service.SavedImageService;
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
    private final SavedImageService savedImageService;
    private final ValidationErrorsService validationErrorsService;
    @PostMapping("/is/liked/by/user")
    public ResponseEntity<List<ImageResponse>> getLikedImage(@RequestBody @Valid UserInformationRequest userInformationRequest, BindingResult bindingResult) throws FileException, ValidationErrorsException, NotFoundDatabaseException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(imageService.getLikedImage(userInformationRequest));
    }

    @PostMapping("/page/data")
    public ResponseEntity<ImagePageResponse> getPageImage(@RequestBody @Valid ImagePageRequest imagePageRequest, BindingResult bindingResult) throws FileException, ValidationErrorsException, NotFoundDatabaseException, NoAddDatabaseException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(imageService.getImageData(imagePageRequest));
    }
    @PostMapping(value = "/add")
    public ResponseEntity<MessageResponse>addImage(@RequestPart("file") MultipartFile multipartFile,@RequestPart @Valid ImageAddRequest imageAddRequest,BindingResult bindingResult) throws ValidationErrorsException, NotFoundDatabaseException, FileException, NoAddDatabaseException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(new MessageResponse(imageService.addImage(imageAddRequest,multipartFile)));
    }
    @PostMapping("/update/data")
    public ResponseEntity<MessageResponse> updateImageData(@RequestBody @Valid ImageUpdateRequest imageUpdateRequest,BindingResult bindingResult) throws ValidationErrorsException, NotFoundDatabaseException, NoAddDatabaseException, ForbiddenRequestException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(new MessageResponse(imageService.updateImageData(imageUpdateRequest)));
    }
    @PostMapping("/add/saved/image")
    public ResponseEntity<MessageResponse> addSavedImage(@RequestBody @Valid SavedImageAddRequest savedImageAddRequest, BindingResult bindingResult) throws ValidationErrorsException, NotFoundDatabaseException, NoAddDatabaseException, ForbiddenRequestException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(new MessageResponse(savedImageService.addSavedImage(savedImageAddRequest)));
    }
    @PostMapping("/created/image")
    public ResponseEntity<List<ImageResponse>> getCreatedImage(@RequestBody @Valid UserInformationRequest userInformationRequest, BindingResult bindingResult) throws ValidationErrorsException, NotFoundDatabaseException, FileException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(imageService.getUserCreatedImages(userInformationRequest));
    }
    @PostMapping("/saved/created/image")
    public ResponseEntity<List<ImageResponse>> getCreatedAndSavedImages(@RequestBody @Valid UserInformationRequest userInformationRequest, BindingResult bindingResult) throws ValidationErrorsException, NotFoundDatabaseException, FileException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(savedImageService.getSavedAndCreatedUserImage(userInformationRequest));
    }
}
