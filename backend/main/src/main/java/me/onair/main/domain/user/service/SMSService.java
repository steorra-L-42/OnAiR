package me.onair.main.domain.user.service;

import me.onair.main.domain.user.error.SMSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class SMSService {

    private static final Logger log = LoggerFactory.getLogger(SMSService.class);
    private final RestClient restClient = RestClient.create();
    @Value("${sms.api.url}")
    private String SMS_API_URL;
    @Value("${sms.api.username}")
    private String SMS_API_USERNAME;
    @Value("${sms.api.key}")
    private String SMS_API_KEY;

    public void sendSMS(String phoneNumber, String code) {

        String url = SMS_API_URL;
        String username = SMS_API_USERNAME;
        String key = SMS_API_KEY;

        // 국제전화번호 형식으로 변환. 예시 : 01012345678 -> 821012345678
        String KoreanPhoneNumber = "82" + phoneNumber.substring(1);
        String requestBody = """
                {
                    "message": "[OnAIR]\\n회원가입 인증번호는 %s 입니다.",
                    "phoneNumbers": ["+%s"]
                }
                """.formatted(code, KoreanPhoneNumber);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(username, key);

        String result = restClient.post()
                .uri(SMS_API_URL)
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .body(requestBody)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        (request, response) -> {
                            log.error("SMSUtil.sendSMS error: {}", response);
                            throw new SMSException();
                        }
                ).body(String.class);
        log.info("SMSUtil.sendSMS result: {}", result);
    }
}
