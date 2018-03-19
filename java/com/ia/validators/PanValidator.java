package com.ia.validators;

import static com.ia.util.RandomGenerator.REGEX_PAN_NUMBER;
import static java.util.regex.Pattern.compile;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.ia.validations.Pan;

public class PanValidator implements ConstraintValidator<Pan, String> {

    private static final Pattern PAN_NUMBER = compile(REGEX_PAN_NUMBER);

    @Override
    public void initialize(final Pan arg0) {}

    @Override
    public boolean isValid(final String input, final ConstraintValidatorContext context) {
        return

        /* Does the input match the expected pattern for Pan number? */
        matchesExpectedPattern(input);
    }

    private boolean matchesExpectedPattern(final String input) {
        final Matcher matcher = PAN_NUMBER.matcher(input);

        return null != matcher && matcher.matches();

    }

}
