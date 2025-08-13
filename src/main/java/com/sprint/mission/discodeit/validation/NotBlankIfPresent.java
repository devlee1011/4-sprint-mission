package com.sprint.mission.discodeit.validation;

import jakarta.validation.Constraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {NotBlankIfPresentValidator.class})
public @interface NotBlankIfPresent {
    String message() default "입력이 없거나 공백이 아니어야 합니다.";

    Class<?>[] groups() default {};
}
