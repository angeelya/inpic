package angeelya.inPic.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = LoginContentValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginContent {
    String message() default "Incorrect login. Use numbers or letters";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
