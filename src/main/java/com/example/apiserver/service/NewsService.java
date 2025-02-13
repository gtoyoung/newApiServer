package com.example.apiserver.service;

import com.example.apiserver.dto.NaverSoccerDto;
import com.example.apiserver.entity.SoccerNews;
import com.example.apiserver.repository.SoccerNewsRepository;
import com.example.apiserver.utils.WordAnalysis;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final SoccerNewsRepository repository;

    private final NaverSoccerNews naverSoccerNews;

    @Cacheable(value = "news")
    public Page<SoccerNews> getNewsList(String search, int page, int size, String date) {

        Page<SoccerNews> newsList = null;

        if (date.isBlank()) {
            if (search.isBlank()) {
                newsList = repository.findAllByOrderByDatetimeDesc(PageRequest.of(page, size));
            } else {
                newsList = repository.findAllByTitleContainingOrSubContentContainingOrderByDatetimeDesc(search, search, PageRequest.of(page, size));
            }
        } else {
            DateTimeFormatter date_formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate ld = LocalDate.parse(date, date_formatter);
            LocalDateTime ldt = LocalDateTime.of(ld, LocalDateTime.now().toLocalTime());
            LocalDateTime startDatetime = LocalDateTime.of(ldt.toLocalDate(), LocalTime.of(0, 0, 0));
            LocalDateTime endDatetime = LocalDateTime.of(ldt.toLocalDate(), LocalTime.of(23, 59, 59));
            newsList = repository.findAllByTitleContainingOrSubContentContainingAndDatetimeBetweenOrderByDatetimeDesc(search, search, startDatetime, endDatetime, PageRequest.of(page, size));
        }

        extractWord(newsList);
        addLink(newsList);

        return newsList;
    }

    private void extractWord(Page<SoccerNews> newsList) {
        newsList.map((news) -> {
            List<String> wordList;
            try {
                wordList = WordAnalysis.doWordNouns(news.getSubContent());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            news.setWords(wordList);

            return news;
        });
    }

    private void addLink(Page<SoccerNews> newsList) {
        newsList.map((news) -> {
            String link = "https://sports.news.naver.com/news" + "?oid=" + news.getOid() + "&aid=" + news.getAid();
            news.setLink(link);

            return news;
        });
    }

    @Transactional
    public void deleteNews() {
        LocalDateTime startDatetime = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.of(0, 0, 0));
        LocalDateTime endDatetime = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalTime.of(23, 59, 59));

        repository.deleteDateTime(startDatetime, endDatetime);
    }

    public void saveNews() {

        try {
            // 오늘 날짜를 가져온다.
            Date nowDate = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

            // 변환 날짜
            String strNowDate = simpleDateFormat.format(nowDate);

            // 전체 페이지 수
            int totalPage = Integer.parseInt(naverSoccerNews.getTotalPages(strNowDate));

            // 전체 페이지 수 만큼 진행
            for (int i = 1; i <= totalPage; i++) {
                // 페이지당 기사 가져오기
                List<NaverSoccerDto> result = naverSoccerNews.getSoccerNews(strNowDate, i);

                // SoccerNews Entity로 변환
                List<SoccerNews> convertList = result.stream().map(SoccerNews::createSoccerNews).collect(Collectors.toList());

                repository.saveAll(convertList);
            }
        } catch (Exception ex){
            ex.getStackTrace();
        }
    }
}
