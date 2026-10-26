package com.jobassistant.job_assistant.repository;

import com.jobassistant.job_assistant.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Long> {

    List<Job> findByTitleContainingIgnoreCase(String title);

    List<Job> findByCompanyContainingIgnoreCase(String company);

    List<Job> findByLocationContainingIgnoreCase(String location);

    List<Job> findByTitleContainingIgnoreCaseAndLocationContainingIgnoreCase(
            String title,
            String location
    );

    List<Job> findBySkillsContainingIgnoreCase(String skills);

    List<Job> findByTitleContainingIgnoreCaseAndLocationContainingIgnoreCaseAndSkillsContainingIgnoreCase(
            String title,
            String location,
            String skills
    );

    List<Job> findByTitleContainingIgnoreCaseAndSkillsContainingIgnoreCase(
            String title,
            String skills
    );

    Optional<Job> findByExternalId(String externalId);
}