package com.jobassistant.job_assistant.controller;

import com.jobassistant.job_assistant.service.AdzunaService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/adzuna")
@CrossOrigin(origins = "http://localhost:5173")
public class AdzunaController {

    private final AdzunaService adzunaService;

    public AdzunaController(AdzunaService adzunaService) {
        this.adzunaService = adzunaService;
    }

    @GetMapping("/search")
    public String searchJobs(
            @RequestParam String what,
            @RequestParam String where) {

        return adzunaService.searchJobs(what, where);
    }
}