package com.planu.group_meeting.valid;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class NullablePatternValidator implements ConstraintValidator<NullablePattern, String> {

    private Pattern pattern;

    @Override
    public void initialize(NullablePattern constraintAnnotation) {
        pattern = Pattern.compile(constraintAnnotation.regexp());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        return pattern.matcher(value).matches();
    }
}
