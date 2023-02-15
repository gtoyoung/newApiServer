package com.example.apiserver.service;

import com.example.apiserver.entity.SoccerNews;
import com.example.apiserver.repository.SoccerNewsRepository;
import com.example.apiserver.utils.WordAnalysis;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final SoccerNewsRepository repository;

    public Page<SoccerNews> getNewsList(String search, int page, int size, String date) {

        Page<SoccerNews> newsList = null;
        if (date.isBlank()) {
            if (search.isBlank()) {
                newsList = repository.findAllByOrderByDatetimeDesc(PageRequest.of(page, size));

                extractWord(newsList);

            } else {
                newsList = repository.findAllByTitleContainingOrSubContentContainingOrderByDatetimeDesc(search, search, PageRequest.of(page, size));

                extractWord(newsList);
            }
        } else {
            DateTimeFormatter date_formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate ld = LocalDate.parse(date, date_formatter);
            LocalDateTime ldt = LocalDateTime.of(ld, LocalDateTime.now().toLocalTime());
            LocalDateTime startDatetime = LocalDateTime.of(ldt.toLocalDate(), LocalTime.of(0, 0, 0));
            LocalDateTime endDatetime = LocalDateTime.of(ldt.toLocalDate(), LocalTime.of(23, 59, 59));
            newsList = repository.findAllByTitleContainingOrSubContentContainingAndDatetimeBetweenOrderByDatetimeDesc(search, search, startDatetime, endDatetime, PageRequest.of(page, size));

            extractWord(newsList);
        }

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
}
