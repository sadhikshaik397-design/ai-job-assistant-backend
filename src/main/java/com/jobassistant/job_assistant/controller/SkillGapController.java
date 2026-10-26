package com.jobassistant.job_assistant.controller;

import com.jobassistant.job_assistant.config.AuthTokenInterceptor;
import com.jobassistant.job_assistant.entity.Job;
import com.jobassistant.job_assistant.entity.UserProfile;
import com.jobassistant.job_assistant.repository.JobRepository;
import com.jobassistant.job_assistant.repository.UserProfileRepository;
import com.jobassistant.job_assistant.service.SkillGapService;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/skill-gap")
public class SkillGapController {

    private final JobRepository jobRepository;
    private final UserProfileRepository userProfileRepository;
    private final SkillGapService skillGapService;

    public SkillGapController(
            JobRepository jobRepository,
            UserProfileRepository userProfileRepository,
            SkillGapService skillGapService) {

        this.jobRepository = jobRepository;
        this.userProfileRepository = userProfileRepository;
        this.skillGapService = skillGapService;
    }

    @GetMapping("/{profileId}/{jobId}")
    public String analyzeSkillGap(
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

        if (profile == null || job == null) {
            return "Profile or Job not found";
        }

        return skillGapService.analyzeSkillGap(profile, job);
    }
}
