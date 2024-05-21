package angeelya.inPic.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueCategoryValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueCategory {
    String message() default "A category already exists";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
