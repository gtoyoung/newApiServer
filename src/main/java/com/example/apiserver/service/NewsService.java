package com.example.apiserver.service;

import com.example.apiserver.entity.SportNews;
import com.example.apiserver.repository.SportsNewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsService {
    private final SportsNewsRepository repository;

    public Page<SportNews> getNewsList(String search, int page, int size, String date) {
        if(date.isBlank()){
            if(search.isBlank()){
                return repository.findAllByOrderByRegistDateDesc(PageRequest.of(page, size));
            } else {
                return repository.findAllByTitleContainingOrderByRegistDateDesc(search, PageRequest.of(page, size));
            }
        } else {
            DateTimeFormatter DATEFORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate ld = LocalDate.parse(date, DATEFORMATTER);
            LocalDateTime ldt = LocalDateTime.of(ld, LocalDateTime.now().toLocalTime());
            LocalDateTime startDatetime = LocalDateTime.of(ldt.toLocalDate(), LocalTime.of(0,0,0));
            LocalDateTime endDatetime = LocalDateTime.of(ldt.toLocalDate(), LocalTime.of(23,59,59));
            return repository.findAllByTitleContainingAndRegistDateBetweenOrderByRegistDateDesc(search, startDatetime, endDatetime, PageRequest.of(page,size));
        }
    }
}
