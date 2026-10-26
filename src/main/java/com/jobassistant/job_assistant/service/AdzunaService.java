package com.jobassistant.job_assistant.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class AdzunaService {

    @Value("${adzuna.app-id}")
    private String appId;

    @Value("${adzuna.app-key}")
    private String appKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String searchJobs(String what, String where) {

        String url = UriComponentsBuilder
                .fromUriString(
                    "https://api.adzuna.com/v1/api/jobs/in/search/1"
                )
                .queryParam("app_id", appId)
                .queryParam("app_key", appKey)
                .queryParam("results_per_page", 20)
                .queryParam("what", what)
                .queryParam("where", where)
                .queryParam("content-type", "application/json")
                .build()
                .encode()
                .toUriString();

        System.out.println("Adzuna search title: " + what);
        System.out.println("Adzuna search location: " + where);

        return restTemplate.getForObject(url, String.class);
    }
}