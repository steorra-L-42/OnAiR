package me.onair.main.kafka.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Topics {
    CHANNEL_INFO(NAMES.CHANNEL_INFO),
    CONTENTS_REQUEST(NAMES.CONTENTS_REQUEST),
    NEWS_REPLY(NAMES.NEWS_REPLY),
    WEATHER_REPLY(NAMES.WEATHER_REPLY),
    STORY(NAMES.STORY),
    STORY_WITH_CHANNEL_INFO(NAMES.STORY_WITH_CHANNEL_INFO),
    STORY_REPLY(NAMES.STORY_REPLY),
    CHANNEL_CLOSE(NAMES.CHANNEL_CLOSE),
    MEDIA(NAMES.MEDIA),
    LOG(NAMES.LOG),
    TEST(NAMES.TEST);

    private final String name;

    public static class NAMES {
        public static final String CHANNEL_INFO = "channel_info_topic";
        public static final String CONTENTS_REQUEST = "contents_request_topic";
        public static final String NEWS_REPLY = "news_reply_topic";
        public static final String WEATHER_REPLY = "weather_reply_topic";
        public static final String STORY = "story_topic";
        public static final String STORY_WITH_CHANNEL_INFO = "story_with_channel_info_topic";
        public static final String STORY_REPLY = "story_reply_topic";
        public static final String CHANNEL_CLOSE = "channel_close_topic";
        public static final String MEDIA = "media_topic";

        public static final String LOG = "log_topic";
        public static final String TEST = "test_topic";
    }
}
