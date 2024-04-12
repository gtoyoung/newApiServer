package com.example.apiserver.repository;

import com.example.apiserver.entity.SoccerNews;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SoccerNewsRepository extends JpaRepository<SoccerNews, Long> {

    Page<SoccerNews> findAllByOrderByDatetimeDesc(PageRequest request);

    Page<SoccerNews> findAllByTitleContainingOrSubContentContainingOrderByDatetimeDesc(String search, String search2, PageRequest request);

    @Query("SELECT u FROM SoccerNews u WHERE 1=1 and (u.title like %?1% or u.subContent like %?2%) and (u.datetime between ?3 and ?4)")
    Page<SoccerNews> findAllByTitleContainingOrSubContentContainingAndDatetimeBetweenOrderByDatetimeDesc(String search, String search2, LocalDateTime before, LocalDateTime after, PageRequest request);

    @Modifying
    @Query("delete from SoccerNews u where u.datetime between ?1 and ?2")//동시성 문제로 직접 쿼리 입력
    void deleteDateTime(LocalDateTime before, LocalDateTime after);
}
