package angeelya.inPic.search.controller;

import angeelya.inPic.database.model.Search;
import angeelya.inPic.database.model.User;
import angeelya.inPic.exception_handling.exception.DatabaseNotFoundException;
import angeelya.inPic.exception_handling.exception.ValidationErrorsException;
import angeelya.inPic.exception_handling.exception.FileException;
import angeelya.inPic.validation.service.ValidationErrorsService;
import angeelya.inPic.search.service.SearchService;
import angeelya.inPic.dto.request.UserInformationRequest;
import angeelya.inPic.dto.request.SearchImageRequest;
import angeelya.inPic.dto.response.ImageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;
    private final ValidationErrorsService validationErrorsService;
    @PostMapping("/images")
    public ResponseEntity<List<ImageResponse>> getImage(@RequestBody @Valid SearchImageRequest searchImageRequest, BindingResult bindingResult) throws ValidationErrorsException, FileException, DatabaseNotFoundException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(searchService.searchImage(searchImageRequest));
    }

    @PostMapping("/history")
    public ResponseEntity<List<Search>> getHistory(@RequestBody @Valid UserInformationRequest userInformationRequest, BindingResult bindingResult) throws ValidationErrorsException, DatabaseNotFoundException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(searchService.getSearchesHistory(userInformationRequest));
    }

}