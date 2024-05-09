package angeelya.inPic.image.controller;

import angeelya.inPic.database.model.Image;
import angeelya.inPic.dto.request.ImagePageRequest;
import angeelya.inPic.dto.request.UserInformationRequest;
import angeelya.inPic.dto.response.ImagePageResponse;
import angeelya.inPic.dto.response.ImageResponse;
import angeelya.inPic.exception_handling.exception.DatabaseNotFoundException;
import angeelya.inPic.exception_handling.exception.FileException;
import angeelya.inPic.exception_handling.exception.ValidationErrorsException;
import angeelya.inPic.validation.service.ValidationErrorsService;
import angeelya.inPic.image.service.ImageService;
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
@RequestMapping("/image")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;
    private final ValidationErrorsService validationErrorsService;
    @PostMapping("/is/liked/by/user")
    public ResponseEntity<List<ImageResponse>> getLikedImage(@RequestBody @Valid UserInformationRequest userInformationRequest, BindingResult bindingResult) throws FileException, ValidationErrorsException, DatabaseNotFoundException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(imageService.getLikedImage(userInformationRequest));
    }

    @PostMapping("/page/data")
    public ResponseEntity<ImagePageResponse> getPageImage(@RequestBody @Valid ImagePageRequest imagePageRequest, BindingResult bindingResult) throws FileException, ValidationErrorsException, DatabaseNotFoundException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(imageService.getImageData(imagePageRequest));
    }
}
