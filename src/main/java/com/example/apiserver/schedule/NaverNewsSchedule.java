package com.example.apiserver.schedule;


import com.example.apiserver.dto.NaverSoccerDto;
import com.example.apiserver.entity.SoccerNews;
import com.example.apiserver.repository.SoccerNewsRepository;
import com.example.apiserver.service.NaverSoccerNews;
import com.example.apiserver.service.NewsService;
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

    private final NewsService newsService;

    //    @Async
    @Scheduled(cron = "0 0 0,3,6,9,12,15,18,21 * * ?")
    public void scheduleTask() throws ParseException {
        // Transaction 동시성 문제로 인한 삭제와 삽입 분기 처리
        newsService.deleteNews();
        newsService.saveNews();
    }
}
