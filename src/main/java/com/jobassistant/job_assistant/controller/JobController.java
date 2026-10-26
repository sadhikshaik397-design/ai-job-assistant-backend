package com.jobassistant.job_assistant.controller;

import com.jobassistant.job_assistant.entity.Job;
import com.jobassistant.job_assistant.repository.JobRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/jobs")
public class JobController {

    private final JobRepository jobRepository;

    public JobController(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @GetMapping
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    @PostMapping
    public Job createJob(@RequestBody Job job) {
        return jobRepository.save(job);
    }

    @GetMapping("/{id}")
    public Job getJobById(@PathVariable Long id) {
        return jobRepository.findById(id).orElse(null);
    }

    @DeleteMapping("/{id}")
    public String deleteJob(@PathVariable Long id) {
        jobRepository.deleteById(id);
        return "Job deleted successfully";
    }

    @GetMapping("/search")
    public List<Job> searchJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String skills) {

        if (title != null && !title.isBlank()) {

            if (location != null && !location.isBlank()) {

                if (skills != null && !skills.isBlank()) {
                    return jobRepository
                            .findByTitleContainingIgnoreCaseAndLocationContainingIgnoreCaseAndSkillsContainingIgnoreCase(
                                    title,
                                    location,
                                    skills
                            );
                }

                return jobRepository
                        .findByTitleContainingIgnoreCaseAndLocationContainingIgnoreCase(
                                title,
                                location
                        );
            }

            if (skills != null && !skills.isBlank()) {
                return jobRepository
                        .findByTitleContainingIgnoreCaseAndSkillsContainingIgnoreCase(
                                title,
                                skills
                        );
            }

            return jobRepository.findByTitleContainingIgnoreCase(title);
        }

        if (company != null && !company.isBlank()) {
            return jobRepository.findByCompanyContainingIgnoreCase(company);
        }

        if (location != null && !location.isBlank()) {
            return jobRepository.findByLocationContainingIgnoreCase(location);
        }

        if (skills != null && !skills.isBlank()) {
            return jobRepository.findBySkillsContainingIgnoreCase(skills);
        }

        return jobRepository.findAll();
    }
}