package com.example.apiserver.service;

import com.example.apiserver.entity.SportNews;
import com.example.apiserver.repository.SportsNewsRepository;
import com.example.apiserver.utils.WordAnalysis;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final SportsNewsRepository repository;

    public Page<SportNews> getNewsList(String search, int page, int size, String date) {

        Page<SportNews> newsList = null;
        if(date.isBlank()){
            if(search.isBlank()){
                newsList = repository.findAllByOrderByRegistDateDesc(PageRequest.of(page, size));

                newsList.map((news)->{
                    List<String> wordList = new ArrayList<>();
                    try {
                        wordList = WordAnalysis.doWordNouns(news.getTitle());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    news.setWords(wordList);

                    return news;
                });

            } else {
                newsList =  repository.findAllByTitleContainingOrderByRegistDateDesc(search, PageRequest.of(page, size));

                newsList.map((news)->{
                    List<String> wordList = new ArrayList<>();
                    try {
                        wordList = WordAnalysis.doWordNouns(news.getTitle());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    news.setWords(wordList);

                    return news;
                });
            }
        } else {
            DateTimeFormatter DATEFORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate ld = LocalDate.parse(date, DATEFORMATTER);
            LocalDateTime ldt = LocalDateTime.of(ld, LocalDateTime.now().toLocalTime());
            LocalDateTime startDatetime = LocalDateTime.of(ldt.toLocalDate(), LocalTime.of(0,0,0));
            LocalDateTime endDatetime = LocalDateTime.of(ldt.toLocalDate(), LocalTime.of(23,59,59));
            newsList = repository.findAllByTitleContainingAndRegistDateBetweenOrderByRegistDateDesc(search, startDatetime, endDatetime, PageRequest.of(page,size));

            newsList.map((news)->{
                List<String> wordList = new ArrayList<>();
                try {
                    wordList = WordAnalysis.doWordNouns(news.getTitle());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                news.setWords(wordList);

                return news;
            });
        }

        return newsList;
    }
}
