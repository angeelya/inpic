package angeelya.inPic.validation.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NameContentValidation implements ConstraintValidator<NameContent,String> {
    @Override
    public void initialize(NameContent constraintAnnotation) {
    }

    @Override
    public boolean isValid(String name, ConstraintValidatorContext constraintValidatorContext) {
        return name!=null&&name.matches("^[A-z-_\\s\\d]+$");
    }
}
