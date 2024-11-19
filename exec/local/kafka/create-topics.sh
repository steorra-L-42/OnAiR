#!/bin/bash

BOOTSTRAP_SERVER="kafka-1:29092"

TOPICS=(
  "channel_info_topic"
  "contents_request_topic"
  "news_reply_topic"
  "weather_reply_topic"
  "story_topic"
  "story_with_channel_info_topic"
  "story_reply_topic"
  "channel_close_topic"
  "log_topic"
)

for topic in "${TOPICS[@]}"; do
  kafka-topics --create --bootstrap-server "$BOOTSTRAP_SERVER" --topic "$topic" || echo "Topic $topic already exists"
done

