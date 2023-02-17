package com.example.apiserver.entity;


import com.example.apiserver.dto.NaverSoccerDto;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@Table(name = "TB_SOCCER_NEWS")
@RequiredArgsConstructor
public class SoccerNews {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column
    private String oid;
    @Column
    private String aid;
    @Column
    private String officeName;
    @Column
    private String title;
    @Column
    private String subContent;
    @Column
    private String thumbnail;
    @Column
    private LocalDateTime datetime;
    @Column
    private String sectionName;

    // 테이블에 적용하지 않음
    @Transient
    private List<String> words;

    @Transient
    private String link;

    @Builder
    public SoccerNews(Long id, String oid, String aid, String officeName, String title, String subContent, String thumbnail, LocalDateTime datetime, String sectionName, List<String> words, String link) {
        this.id = id;
        this.oid = oid;
        this.aid = aid;
        this.officeName = officeName;
        this.title = title;
        this.subContent = subContent;
        this.thumbnail = thumbnail;
        this.datetime = datetime;
        this.sectionName = sectionName;
        this.words = words;
        this.link = link;
    }


    public static SoccerNews createSoccerNews(NaverSoccerDto naverSoccerDto) {
        SoccerNews news = SoccerNews.builder().oid(naverSoccerDto.getOid())
                .aid(naverSoccerDto.getAid())
                .officeName(naverSoccerDto.getOfficeName())
                .title(naverSoccerDto.getTitle())
                .subContent(naverSoccerDto.getSubContent())
                .thumbnail(naverSoccerDto.getThumbnail())
                .datetime(convertStringtoDate(naverSoccerDto.getDatetime()))
                .sectionName(naverSoccerDto.getSectionName())
                .build();
        return news;
    }

    /**
     * 문자열을 LocalDateTime 형태로 변환
     *
     * @param date
     * @return
     */
    private static LocalDateTime convertStringtoDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
        return dateTime;
    }
}
