package com.jobassistant.job_assistant.controller;

import com.jobassistant.job_assistant.entity.Job;
import com.jobassistant.job_assistant.repository.JobRepository;
import com.jobassistant.job_assistant.service.AIService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    private final AIService aiService;
    private final JobRepository jobRepository;

    public AIController(AIService aiService, JobRepository jobRepository) {
        this.aiService = aiService;
        this.jobRepository = jobRepository;
    }

    @GetMapping("/analyze/{jobId}")
    public String analyzeJob(@PathVariable Long jobId) {

        Job job = jobRepository.findById(jobId).orElse(null);

        if (job == null) {
            return "Job not found";
        }

        return aiService.analyzeJob(job.getDescription());
    }
}