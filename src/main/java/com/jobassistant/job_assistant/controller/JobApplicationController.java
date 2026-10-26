package com.jobassistant.job_assistant.controller;

import com.jobassistant.job_assistant.config.AuthTokenInterceptor;
import com.jobassistant.job_assistant.entity.JobApplication;
import com.jobassistant.job_assistant.repository.JobApplicationRepository;
import com.jobassistant.job_assistant.repository.JobRepository;
import com.jobassistant.job_assistant.repository.UserProfileRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.Set;

@RestController
@RequestMapping("/api/applications")
public class JobApplicationController {
    private static final Set<String> STATUSES = Set.of(
            "APPLIED", "SHORTLISTED", "INTERVIEW", "SELECTED", "REJECTED");

    private final JobApplicationRepository applications;
    private final UserProfileRepository profiles;
    private final JobRepository jobs;

    public JobApplicationController(JobApplicationRepository applications,
                                    UserProfileRepository profiles,
                                    JobRepository jobs) {
        this.applications = applications;
        this.profiles = profiles;
        this.jobs = jobs;
    }

    private Long currentUserId(HttpServletRequest request) {
        return (Long) request.getAttribute(AuthTokenInterceptor.USER_ID_ATTRIBUTE);
    }

    @GetMapping
    public List<JobApplication> getMyApplications(HttpServletRequest request) {
        return applications.findAllByUserId(currentUserId(request));
    }

    @PostMapping
    public ResponseEntity<?> createApplication(@RequestBody JobApplication application,
                                               HttpServletRequest request) {
        Long userId = currentUserId(request);
        if (application.getProfileId() == null || application.getJobId() == null) {
            return ResponseEntity.badRequest().body("Profile ID and job ID are required.");
        }
        if (profiles.findByIdAndUserId(application.getProfileId(), userId).isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Profile does not belong to this account.");
        }
        if (!jobs.existsById(application.getJobId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Job not found.");
        }
        if (applications.existsByUserIdAndJobId(userId, application.getJobId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("This job is already tracked.");
        }
        application.setId(null);
        application.setUserId(userId);
        application.setStatus(cleanStatus(application.getStatus()));
        return ResponseEntity.status(HttpStatus.CREATED).body(applications.save(application));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobApplication> getApplicationById(@PathVariable Long id,
                                                             HttpServletRequest request) {
        return applications.findByIdAndUserId(id, currentUserId(request))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateApplication(@PathVariable Long id,
                                               @RequestBody JobApplication updated,
                                               HttpServletRequest request) {
        return applications.findByIdAndUserId(id, currentUserId(request))
                .<ResponseEntity<?>>map(application -> {
                    try {
                        application.setStatus(cleanStatus(updated.getStatus()));
                    } catch (IllegalArgumentException exception) {
                        return ResponseEntity.badRequest().body(exception.getMessage());
                    }
                    application.setAppliedDate(updated.getAppliedDate());
                    application.setInterviewDate(updated.getInterviewDate());
                    application.setNotes(updated.getNotes());
                    return ResponseEntity.ok(applications.save(application));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id,
                                          @RequestParam String status,
                                          HttpServletRequest request) {
        return applications.findByIdAndUserId(id, currentUserId(request))
                .<ResponseEntity<?>>map(application -> {
                    try {
                        application.setStatus(cleanStatus(status));
                    } catch (IllegalArgumentException exception) {
                        return ResponseEntity.badRequest().body(exception.getMessage());
                    }
                    return ResponseEntity.ok(applications.save(application));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApplication(@PathVariable Long id,
                                                  HttpServletRequest request) {
        return applications.findByIdAndUserId(id, currentUserId(request))
                .map(application -> {
                    applications.delete(application);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private String cleanStatus(String status) {
        String value = (status == null || status.isBlank())
                ? "APPLIED" : status.trim().toUpperCase(Locale.ROOT);
        if (!STATUSES.contains(value)) {
            throw new IllegalArgumentException("Status must be one of: " + STATUSES);
        }
        return value;
    }
}
