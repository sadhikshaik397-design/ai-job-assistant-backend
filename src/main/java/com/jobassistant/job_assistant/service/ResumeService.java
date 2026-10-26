package com.jobassistant.job_assistant.service;

import com.jobassistant.job_assistant.entity.Job;
import com.jobassistant.job_assistant.entity.UserProfile;
import org.springframework.stereotype.Service;

@Service
public class ResumeService {

    public String generateTailoredResume(UserProfile profile, Job job) {

        String name = profile.getName() == null
                ? ""
                : profile.getName();

        String education = profile.getEducation() == null
                ? ""
                : profile.getEducation();

        String skills = profile.getSkills() == null
                ? ""
                : profile.getSkills();

        String experience = profile.getExperience() == null
                ? ""
                : profile.getExperience();

        String jobTitle = job.getTitle() == null
                ? ""
                : job.getTitle();

        String company = job.getCompany() == null
                ? ""
                : job.getCompany();

        return """
                PROFESSIONAL SUMMARY

                %s is a %s fresher with skills in %s. He is seeking an opportunity
                as a %s at %s to apply his programming and software development skills
                and continue building his technical knowledge.


                RELEVANT SKILLS

                %s


                RELEVANT EXPERIENCE/PROJECTS

                %s


                CAREER OBJECTIVE

                To begin my career as a %s and apply my skills in %s to contribute
                to software development projects at %s. I aim to continuously improve
                my technical knowledge and gain practical experience in the software
                development industry.
                """.formatted(
                name,
                education,
                skills,
                jobTitle,
                company,
                skills,
                getExperienceText(experience),
                jobTitle,
                skills,
                company
        );
    }

    private String getExperienceText(String experience) {

        if (experience.isBlank()
                || experience.equalsIgnoreCase("Fresher")) {

            return "No specific projects or professional experience were provided.";
        }

        return experience;
    }
}