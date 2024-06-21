package com.example.apiserver.service;

import com.example.apiserver.dto.FCMMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

@RequiredArgsConstructor
//@Service
public class FCMService {


    private final String API_URL = "https://fcm.googleapis.com/v1/projects/messagetest-e5c56/messages:send";
    private final ObjectMapper objectMapper;
//    @Value("${spring.fcm.key.path}")
    private String googleApplicationCredentials;
    // 메시징만 권한 설정
    @Value("${spring.fcm.key.scope}")
    private String fireBaseScope;
    // oauth2 인증대상
    private GoogleCredentials googleCredentials;

    @PostConstruct
    public void initialize() throws IOException {
        ClassPathResource resource = new ClassPathResource(googleApplicationCredentials);

        googleCredentials = GoogleCredentials.fromStream(resource.getInputStream()).createScoped(fireBaseScope);
    }

    private String getAccessToken() throws IOException {
        // AccessToken 생성
        googleCredentials.refreshIfExpired();

        // AccessToken 반환
        return googleCredentials.getAccessToken().getTokenValue();
    }

    private String makeMessage(String targetToken, String title, String body) throws JsonProcessingException {
        FCMMessage fcmMessage = FCMMessage.builder().message(FCMMessage.Message.builder()
                        .token(targetToken)
                        .notification(FCMMessage.Notification.builder()
                                .title(title)
                                .body(body)
                                .image(null)
                                .build()
                        )
                        .build()
                )
                .validate_only(false)
                .build();
        return objectMapper.writeValueAsString(fcmMessage);
    }

    public void sendMessageTo(String targetToken, String title, String body) throws IOException {
        String message = makeMessage(targetToken, title, body);

        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), message);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(requestBody)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + getAccessToken())
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json; UTF-8")
                .build();

        Response response = client.newCall(request)
                .execute();

        System.out.println(response.body().string());

    }


}
