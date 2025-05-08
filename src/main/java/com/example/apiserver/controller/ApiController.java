package com.example.apiserver.controller;

import com.example.apiserver.entity.SoccerNews;
import com.example.apiserver.service.NewsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequiredArgsConstructor
@Api(tags = {"해축 API"})
public class ApiController {

    private final NewsService service;
    private final CacheManager cacheManager;

    @GetMapping(value = "/newsList")
    @ApiOperation(value = "해외 축구 기사", response = SoccerNews.class)
    public Page<SoccerNews> getNews(@RequestParam(required = false, defaultValue = "") String search, @RequestParam(required = false, defaultValue = "") int page, @RequestParam(required = false, defaultValue = "") int size, @RequestParam(required = false, defaultValue = "") String date) {
        return service.getNewsList(search, page, size, date);
    }

    @ApiIgnore
    @GetMapping(value = "/schedule")
    public String getSchedule() throws ParseException {
        service.deleteNews();
        service.saveNews();
        return "OK";
    }

    @ApiIgnore
    @GetMapping(value = "/clearCache")
    public String clearCache() {
        cacheManager.getCache("news").clear();
        return "OK";
    }
}
