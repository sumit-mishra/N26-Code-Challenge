package com.n26.controller;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.n26.service.StatisticsService;


@RestController
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Autowired
    public StatisticsController(final StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @ResponseStatus(OK)
    @GetMapping(value = "/statistics", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getStatistcs() {
        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<String>(statisticsService.prepare().toString(), httpHeaders, HttpStatus.OK);

        // return statisticsService.prepare().toString();
    }
}
