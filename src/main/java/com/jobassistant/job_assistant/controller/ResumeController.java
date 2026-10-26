package com.jobassistant.job_assistant.controller;

import com.jobassistant.job_assistant.config.AuthTokenInterceptor;
import com.jobassistant.job_assistant.entity.Job;
import com.jobassistant.job_assistant.entity.UserProfile;
import com.jobassistant.job_assistant.repository.JobRepository;
import com.jobassistant.job_assistant.repository.UserProfileRepository;
import com.jobassistant.job_assistant.service.ResumeService;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/resume")
public class ResumeController {

    private final JobRepository jobRepository;
    private final UserProfileRepository userProfileRepository;
    private final ResumeService resumeService;

    public ResumeController(
            JobRepository jobRepository,
            UserProfileRepository userProfileRepository,
            ResumeService resumeService) {

        this.jobRepository = jobRepository;
        this.userProfileRepository = userProfileRepository;
        this.resumeService = resumeService;
    }

    @GetMapping("/tailor/{profileId}/{jobId}")
    public String tailorResume(
            @PathVariable Long profileId,
            @PathVariable Long jobId,
            HttpServletRequest request) {

        UserProfile profile = userProfileRepository
                .findByIdAndUserId(profileId,
                        (Long) request.getAttribute(AuthTokenInterceptor.USER_ID_ATTRIBUTE))
                .orElse(null);

        Job job = jobRepository
                .findById(jobId)
                .orElse(null);

        if (profile == null) {
            return "Profile not found";
        }

        if (job == null) {
            return "Job not found";
        }

        return resumeService.generateTailoredResume(profile, job);
    }
}
