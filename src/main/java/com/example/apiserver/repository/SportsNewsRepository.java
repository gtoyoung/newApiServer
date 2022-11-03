package com.example.apiserver.repository;

import com.example.apiserver.entity.SportNews;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface SportsNewsRepository extends JpaRepository<SportNews, Long> {

    Page<SportNews> findAllByOrderByRegistDateDesc(PageRequest request);
    Page<SportNews> findAllByTitleContainingOrderByRegistDateDesc(String search, PageRequest request);
    Page<SportNews> findAllByTitleContainingAndRegistDateBetweenOrderByRegistDateDesc(String search, LocalDateTime before, LocalDateTime after, PageRequest request);
}
