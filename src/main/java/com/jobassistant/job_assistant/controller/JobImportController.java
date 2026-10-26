package com.jobassistant.job_assistant.controller;

import com.jobassistant.job_assistant.entity.Job;
import com.jobassistant.job_assistant.repository.JobRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/import")
public class JobImportController {

    private final JobRepository jobRepository;

    public JobImportController(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @PostMapping("/job")
    public Job importJob(@RequestBody Job job) {

        if (job.getExternalId() != null &&
                !job.getExternalId().isBlank()) {

            return jobRepository
                    .findByExternalId(job.getExternalId())
                    .orElseGet(() -> jobRepository.save(job));
        }

        return jobRepository.save(job);
    }
}