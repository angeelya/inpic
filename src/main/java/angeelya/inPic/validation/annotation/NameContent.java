package angeelya.inPic.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NameContentValidation.class)
@Target({ElementType.PARAMETER, ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NameContent {
    String message() default "Incorrect name. Use letters and numbers";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
