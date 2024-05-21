package angeelya.inPic.validation.annotation;

import angeelya.inPic.database.model.Category;
import angeelya.inPic.database.model.User;
import angeelya.inPic.database.repository.CategoryRepository;
import angeelya.inPic.database.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
@Component
@RequiredArgsConstructor
public class UniqueCategoryValidator implements ConstraintValidator<UniqueCategory,String> {
    private final CategoryRepository categoryRepository;

    @Override
    public void initialize(UniqueCategory constraintAnnotation) {
    }

    @Override
    public boolean isValid(String category, ConstraintValidatorContext constraintValidatorContext) {
        Optional<Category> categoryOptional = categoryRepository.findByCategory(category);
        return categoryOptional.isEmpty();
    }
}
