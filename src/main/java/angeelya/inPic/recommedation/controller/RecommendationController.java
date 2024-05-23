package angeelya.inPic.recommedation.controller;

import angeelya.inPic.dto.request.UserInformationRequest;
import angeelya.inPic.dto.response.CategoryRecommendationResponse;
import angeelya.inPic.dto.response.ImageRecommendationResponse;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
import angeelya.inPic.exception_handling.exception.ValidationErrorsException;
import angeelya.inPic.recommedation.service.CategoryRecommendationService;
import angeelya.inPic.recommedation.service.ImageRecommendationService;
import angeelya.inPic.validation.service.ValidationErrorsService;
import angeelya.inPic.exception_handling.exception.FileException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/recommend")
@RequiredArgsConstructor
public class RecommendationController {
    private final ImageRecommendationService imageRecommendationService;
    private final CategoryRecommendationService categoryRecommendationService;
    private final ValidationErrorsService validationErrorsService;

    @PostMapping("/image")
    public ResponseEntity<List<ImageRecommendationResponse>> getImageRecommendations(@RequestBody @Valid UserInformationRequest userInformationRequest, BindingResult bindingResult) throws ValidationErrorsException, NotFoundDatabaseException, FileException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(imageRecommendationService.getRecommendations(userInformationRequest));
    }

    @PostMapping("/category")
    public ResponseEntity<List<CategoryRecommendationResponse>> getCategoryRecommendations(@RequestBody @Valid UserInformationRequest userInformationRequest, BindingResult bindingResult) throws ValidationErrorsException, NotFoundDatabaseException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(categoryRecommendationService.getRecommendations(userInformationRequest));
    }

    @GetMapping("/popular/category")
    public ResponseEntity<List<CategoryRecommendationResponse>> getPopularCategory() throws NotFoundDatabaseException {
        return ResponseEntity.ok(categoryRecommendationService.getPopularRecommendations());
    }
}
