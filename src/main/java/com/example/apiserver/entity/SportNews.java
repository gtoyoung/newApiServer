package com.example.apiserver.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "TB_SPORT_NEWS")
public class SportNews {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @Column
    private String title;
    @Column
    private String image;
    @Column
    private String link;
    @Column
    private LocalDateTime registDate;
}
