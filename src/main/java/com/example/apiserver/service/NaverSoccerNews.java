package com.example.apiserver.service;


import com.example.apiserver.dto.NaverSoccerDto;
import com.google.gson.Gson;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NaverSoccerNews {

    private final String baseUrl = "https://sports.news.naver.com/wfootball/news/list?isphoto=N";

    private static Object[] getMapFromJsonObject(String jsonStr, Class targetClass) {
        Gson gson = new Gson();
        Object[] result = (Object[]) gson.fromJson(jsonStr, targetClass);

        return result;
    }

    private JSONObject getTargetObj(String url) throws ParseException {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<?> entity = new HttpEntity<>(headers);

        UriComponents uri = UriComponentsBuilder.fromHttpUrl(baseUrl + "&" + url).build();

        String response = restTemplate.getForObject(uri.toUri(), String.class);

        JSONParser parser = new JSONParser();
        JSONObject res = (JSONObject) parser.parse(response);

        return res;
    }

    /**
     * 요청한 날짜 기사에 대한 총 페이지 수
     *
     * @param date
     * @return
     * @throws ParseException
     */
    public String getTotalPages(String date) throws ParseException {
        String targetUrl = "&date=" + date;

        JSONObject res = getTargetObj(targetUrl);

        String totalPages = res.get("totalPages").toString();

        return totalPages;
    }

    /**
     * 축구 뉴스 api 호출
     *
     * @param date
     * @param page
     * @return
     * @throws ParseException
     */
    public List<NaverSoccerDto> getSoccerNews(String date, int page) throws ParseException {
        String targetUrl = "&date=" + date + "&page=" + page;

        JSONObject res = getTargetObj(targetUrl);

        // Item List
        String list = res.get("list").toString();

        NaverSoccerDto[] convertList = (NaverSoccerDto[]) getMapFromJsonObject(list, NaverSoccerDto[].class);

        return Arrays.stream(convertList).collect(Collectors.toList());
    }
}
