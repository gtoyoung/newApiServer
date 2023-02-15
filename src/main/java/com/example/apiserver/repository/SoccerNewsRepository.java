package com.example.apiserver.repository;

import com.example.apiserver.entity.SoccerNews;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface SoccerNewsRepository extends JpaRepository<SoccerNews, Long> {

    Page<SoccerNews> findAllByOrderByDatetimeDesc(PageRequest request);

    Page<SoccerNews> findAllByTitleContainingOrSubContentContainingOrderByDatetimeDesc(String search, String search2, PageRequest request);

    Page<SoccerNews> findAllByTitleContainingOrSubContentContainingAndDatetimeBetweenOrderByDatetimeDesc(String search, String search2, LocalDateTime before, LocalDateTime after, PageRequest request);

    void deleteByDatetimeBetween(LocalDateTime before, LocalDateTime after);
}
