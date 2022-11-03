package com.example.apiserver.controller;

import com.example.apiserver.entity.SportNews;
import com.example.apiserver.service.NewsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Api(tags = {"해외 축구 기사 API"})
public class ApiController {

    private final NewsService service;

    @GetMapping(value = "/newsList")
    @ApiOperation(value = "해외 축구 기사", response = SportNews.class)
    public Page<SportNews> getNews(@RequestParam(required = false, defaultValue = "") String search, @RequestParam(required = false, defaultValue = "") int page, @RequestParam(required = false, defaultValue = "") int size, @RequestParam(required = false, defaultValue = "") String date) {
        return service.getNewsList(search, page, size,date);
    }
}
