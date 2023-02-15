package com.example.apiserver.schedule;


import com.example.apiserver.dto.NaverSoccerDto;
import com.example.apiserver.entity.SoccerNews;
import com.example.apiserver.repository.SoccerNewsRepository;
import com.example.apiserver.service.NaverSoccerNews;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NaverNewsSchedule {

    private final SoccerNewsRepository soccerNewsRepository;

    private final NaverSoccerNews naverSoccerNews;

    @Async
    //@Scheduled(cron = "0 35 15 * * ?")
    @Scheduled(cron = "0 0 0,3,6,9,12,15,18,21 * * ?")
    @Transactional
    public void scheduleTask() throws ParseException {
        // 오늘 날짜를 가져온다.
        Date nowDate = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

        // 변환 날짜
        String strNowDate = simpleDateFormat.format(nowDate);

        // 전체 페이지 수
        int totalPage = Integer.parseInt(naverSoccerNews.getTotalPages(strNowDate));

        // 삽입할 데이터 껍데기
        List<SoccerNews> saveList = new ArrayList<>();

        // 전체 페이지 수 만큼 진행
        for (int i = 1; i <= totalPage; i++) {
            // 페이지당 기사 가져오기
            List<NaverSoccerDto> result = naverSoccerNews.getSoccerNews(strNowDate, i);

            // SoccerNews Entity로 변환
            List<SoccerNews> convertList = result.stream().map(SoccerNews::createSoccerNews).collect(Collectors.toList());

            saveList.addAll(convertList);
        }

        LocalDateTime startDatetime = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.of(0, 0, 0));
        LocalDateTime endDatetime = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.of(23, 59, 59));

        soccerNewsRepository.deleteByDatetimeBetween(startDatetime, endDatetime);

        soccerNewsRepository.saveAll(saveList);
    }
}
