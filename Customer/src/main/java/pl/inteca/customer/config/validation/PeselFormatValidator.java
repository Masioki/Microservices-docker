package pl.inteca.customer.config.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Pesel format (11 numeric characters) validator
 */
public class PeselFormatValidator implements ConstraintValidator<Pesel, String> {
    @Override
    public void initialize(Pesel constraintAnnotation) {
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s != null && s.matches("^[0-9]{11}$");
    }
}
