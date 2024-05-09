package angeelya.inPic.validation.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CategoryContentValidation implements ConstraintValidator<CategoryContent,String> {
    @Override
    public void initialize(CategoryContent constraintAnnotation) {
    }

    @Override
    public boolean isValid(String category, ConstraintValidatorContext constraintValidatorContext) {
        return category!=null&&category.matches("^[a-zA-Z ]+$");
    }
}
