package angeelya.inPic.image.controller;

import angeelya.inPic.database.model.Category;
import angeelya.inPic.dto.request.CategoryAddingRequest;
import angeelya.inPic.dto.response.MessageResponse;
import angeelya.inPic.exception_handling.exception.NotFoundDatabaseException;
import angeelya.inPic.exception_handling.exception.NoAddDatabaseException;
import angeelya.inPic.exception_handling.exception.ValidationErrorsException;
import angeelya.inPic.image.service.CategoryService;
import angeelya.inPic.validation.service.ValidationErrorsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final ValidationErrorsService validationErrorsService;

    @GetMapping("/all")
    public ResponseEntity<List<Category>> getAllCategories() throws NotFoundDatabaseException {
        return ResponseEntity.ok(categoryService.getAllCategory());
    }

    @PostMapping("/add")
    public ResponseEntity<MessageResponse> addCategory(@RequestBody @Valid CategoryAddingRequest categoryAddingRequest, BindingResult bindingResult) throws NoAddDatabaseException, ValidationErrorsException {
        validationErrorsService.validation(bindingResult);
        return ResponseEntity.ok(new MessageResponse(categoryService.addCategory(categoryAddingRequest)));
    }
}
