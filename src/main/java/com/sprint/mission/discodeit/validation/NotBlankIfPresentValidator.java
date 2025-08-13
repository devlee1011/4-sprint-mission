package com.sprint.mission.discodeit.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

public class NotBlankIfPresentValidator implements ConstraintValidator<NotBlankIfPresent, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // null은 허용, 공백 문자열인 경우 false 반환
        return value == null || StringUtils.hasText(value);
    }
}
