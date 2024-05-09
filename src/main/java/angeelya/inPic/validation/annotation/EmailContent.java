package angeelya.inPic.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EmailContentValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EmailContent {
    String message() default "Incorrect email. Use the pattern like example user@gmail.com ";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
