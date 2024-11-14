package me.onair.main.domain.story.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = StoryMusicValidator.class)
public @interface ValidMusic {
    String message() default "All music fields must be either all provided or all omitted.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
