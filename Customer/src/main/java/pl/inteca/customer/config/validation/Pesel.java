package pl.inteca.customer.config.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PeselFormatValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Pesel {
    String message() default "Pesel format is 11 numerical characters";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
