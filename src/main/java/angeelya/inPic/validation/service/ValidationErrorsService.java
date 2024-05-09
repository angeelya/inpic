package angeelya.inPic.validation.service;

import angeelya.inPic.exception_handling.exception.ValidationErrorsException;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.stream.Collectors;

@Service
public class ValidationErrorsService {
    public void validation(BindingResult bindingResult) throws ValidationErrorsException {
        if (bindingResult.hasErrors()) {
            throw new ValidationErrorsException(getValidationErrors(bindingResult));
        }
    }
    private   String getValidationErrors(BindingResult bindingResult){
        return bindingResult.getAllErrors().stream()
                .map(objectError -> objectError.getDefaultMessage())
                .collect(Collectors.joining(". "));
    }
}
