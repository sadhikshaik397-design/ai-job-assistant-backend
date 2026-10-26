package com.jobassistant.job_assistant.controller;

import com.jobassistant.job_assistant.config.AuthTokenInterceptor;
import com.jobassistant.job_assistant.entity.Job;
import com.jobassistant.job_assistant.entity.UserProfile;
import com.jobassistant.job_assistant.repository.JobRepository;
import com.jobassistant.job_assistant.repository.UserProfileRepository;
import com.jobassistant.job_assistant.service.JobMatchingService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.List;

@RestController
@RequestMapping("/api/matching")
public class JobMatchingController {

    private final JobRepository jobRepository;
    private final UserProfileRepository userProfileRepository;
    private final JobMatchingService jobMatchingService;

    public JobMatchingController(
            JobRepository jobRepository,
            UserProfileRepository userProfileRepository,
            JobMatchingService jobMatchingService) {

        this.jobRepository = jobRepository;
        this.userProfileRepository = userProfileRepository;
        this.jobMatchingService = jobMatchingService;
    }

    @GetMapping("/{profileId}/{jobId}")
    public Map<String, Object> calculateMatch(
            @PathVariable Long profileId,
            @PathVariable Long jobId,
            HttpServletRequest request) {

        UserProfile profile = userProfileRepository
                .findByIdAndUserId(profileId, authenticatedUserId(request))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Profile not found"));

        Job job = jobRepository
                .findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Job not found"));

        return buildMatchResponse(profile, job);
    }

    @GetMapping("/search/{profileId}")
    public List<Map<String, Object>> searchAndMatchJobs(
            @PathVariable Long profileId,
            @RequestParam String title,
            HttpServletRequest request) {

        UserProfile profile = userProfileRepository
                .findByIdAndUserId(profileId, authenticatedUserId(request))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Profile not found"));

        return jobRepository
                .findByTitleContainingIgnoreCase(title)
                .stream()
                .map(job -> buildMatchResponse(profile, job))
                .toList();
    }

    private Long authenticatedUserId(HttpServletRequest request) {
        return (Long) request.getAttribute(AuthTokenInterceptor.USER_ID_ATTRIBUTE);
    }

    private Map<String, Object> buildMatchResponse(
            UserProfile profile,
            Job job) {

        int score = jobMatchingService
                .calculateMatchScore(profile, job);

        Set<String> matchedSkills = new TreeSet<>(
                jobMatchingService.getMatchedSkills(profile, job));

        Set<String> missingSkills = new TreeSet<>(
                jobMatchingService.getMissingSkills(profile, job));

        Set<String> requiredSkills = new TreeSet<>(
                jobMatchingService.getRequiredSkills(job));

        Set<String> preferredSkills = new TreeSet<>(
                jobMatchingService.getPreferredSkills(job));

        Set<String> matchedRequiredSkills = new TreeSet<>(
                jobMatchingService.getMatchedRequiredSkills(profile, job));

        Set<String> missingRequiredSkills = new TreeSet<>(
                jobMatchingService.getMissingRequiredSkills(profile, job));

        Set<String> matchedPreferredSkills = new TreeSet<>(
                jobMatchingService.getMatchedPreferredSkills(profile, job));

        Set<String> missingPreferredSkills = new TreeSet<>(
                jobMatchingService.getMissingPreferredSkills(profile, job));

        Map<String, Object> result = new LinkedHashMap<>();

        result.put("jobId", job.getId());
        result.put("jobTitle", job.getTitle());
        result.put("company", job.getCompany());
        result.put("location", job.getLocation());

        result.put("matchScore", score);
        result.put("recommendation",
                jobMatchingService.getRecommendation(score));
        result.put("experienceMatchMessage",
                jobMatchingService.getExperienceMatchMessage(profile, job));

        result.put("matchedSkills", matchedSkills);
        result.put("missingSkills", missingSkills);

        result.put("requiredSkills", requiredSkills);
        result.put("preferredSkills", preferredSkills);

        result.put("matchedRequiredSkills", matchedRequiredSkills);
        result.put("missingRequiredSkills", missingRequiredSkills);

        result.put("matchedPreferredSkills", matchedPreferredSkills);
        result.put("missingPreferredSkills", missingPreferredSkills);

        return result;
    }
}
