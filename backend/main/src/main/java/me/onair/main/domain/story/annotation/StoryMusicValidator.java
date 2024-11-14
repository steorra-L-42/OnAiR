package me.onair.main.domain.story.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;
import me.onair.main.domain.story.dto.StoryCreateRequest.Music;

public class StoryMusicValidator implements ConstraintValidator<ValidMusic, Music> {

    // URL 검증을 위한 정규식 (http:// 또는 https://로 시작)
    private static final Pattern URL_PATTERN = Pattern.compile(
            "^(http|https)://[\\w.-]+(?:\\.[a-z]{2,})+(?:/[^\\s]*)?$");

    private static final int MAX_TITLE_LENGTH = 150;
    private static final int MAX_ARTIST_LENGTH = 100;

    @Override
    public boolean isValid(Music music, ConstraintValidatorContext context) {
        if (music == null) {
            return true; // Music 객체가 null이면 검사하지 않음 (다음 단계에서 `@NotNull`로 검사 가능)
        }

        // 모든 필드가 null, empty이거나 모든 필드가 존재해야 함
        boolean allFieldsNull =
                (music.getMusicTitle() == null || music.getMusicTitle().trim().isEmpty()) &&
                        (music.getMusicArtist() == null || music.getMusicArtist().trim().isEmpty()) &&
                        (music.getMusicCoverUrl() == null || music.getMusicCoverUrl().trim().isEmpty());

        boolean allFieldsPresent =
                (music.getMusicTitle() != null && !music.getMusicTitle().trim().isEmpty()) &&
                        (music.getMusicArtist() != null && !music.getMusicArtist().trim().isEmpty()) &&
                        (music.getMusicCoverUrl() != null && !music.getMusicCoverUrl().trim().isEmpty());

        // 조건이 맞지 않으면 오류 메시지 설정
        if (!(allFieldsNull || allFieldsPresent)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("All music fields must be either all provided or all omitted.")
                    .addConstraintViolation();
            return false;
        }

        // musicCoverUrl이 존재하면 URL 형식 검증
        if (music.getMusicCoverUrl() != null && !URL_PATTERN.matcher(music.getMusicCoverUrl()).matches()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Invalid URL format for musicCoverUrl.")
                    .addConstraintViolation();
            return false;
        }

        // musicTitle 길이 검증 (최대 150자)
        if (music.getMusicTitle() != null && music.getMusicTitle().length() > MAX_TITLE_LENGTH) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Music title must be at most 150 characters long.")
                    .addConstraintViolation();
            return false;
        }

        // musicArtist 길이 검증 (최대 100자)
        if (music.getMusicArtist() != null && music.getMusicArtist().length() > MAX_ARTIST_LENGTH) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Music artist name must be at most 100 characters long.")
                    .addConstraintViolation();
            return false;
        }

        return true; // 모든 검증을 통과하면 유효
    }
}
