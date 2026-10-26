package com.jobassistant.job_assistant.repository;

import com.jobassistant.job_assistant.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobApplicationRepository
        extends JpaRepository<JobApplication, Long> {
    java.util.List<JobApplication> findAllByUserId(Long userId);
    java.util.Optional<JobApplication> findByIdAndUserId(Long id, Long userId);
    boolean existsByUserIdAndJobId(Long userId, Long jobId);
}
