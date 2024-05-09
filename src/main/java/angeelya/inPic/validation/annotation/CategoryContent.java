package angeelya.inPic.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CategoryContentValidation.class)
@Target({ElementType.PARAMETER, ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CategoryContent {
    String message() default "Incorrect category. Use letters";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
