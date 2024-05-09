package angeelya.inPic.validation.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LoginContentValidator implements ConstraintValidator<LoginContent,String> {
    @Override
    public void initialize(LoginContent constraintAnnotation) {
    }

    @Override
    public boolean isValid(String login, ConstraintValidatorContext constraintValidatorContext) {
        return login!=null&&login.matches("^[\\dA-z]+$");
    }
}
