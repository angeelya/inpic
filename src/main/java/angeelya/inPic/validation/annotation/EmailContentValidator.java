package angeelya.inPic.validation.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmailContentValidator implements ConstraintValidator<EmailContent,String> {
    @Override
    public void initialize(EmailContent constraintAnnotation) {
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {

        return email!=null&&email.matches("[\\w-._%+-]+@[A-z\\d-]+.[a-z]{2,9}");
    }
}
