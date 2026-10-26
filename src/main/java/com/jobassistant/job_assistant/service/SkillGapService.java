package com.jobassistant.job_assistant.service;

import com.jobassistant.job_assistant.entity.Job;
import com.jobassistant.job_assistant.entity.UserProfile;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class SkillGapService {

    public String analyzeSkillGap(UserProfile profile, Job job) {

        if (profile.getSkills() == null || job.getDescription() == null) {
            return "Skills information is missing.";
        }

        Set<String> userSkills = extractUserSkills(profile.getSkills());
        Set<String> requiredSkills = extractRequiredSkills(job.getDescription());

        if (requiredSkills.isEmpty()) {
            return "No technical skills found in the job description.";
        }

        Set<String> matchedSkills = new HashSet<>(userSkills);
        matchedSkills.retainAll(requiredSkills);

        Set<String> missingSkills = new HashSet<>(requiredSkills);
        missingSkills.removeAll(userSkills);

        int matchPercentage =
                (matchedSkills.size() * 100) / requiredSkills.size();

        return "Matched Skills: " + matchedSkills
                + "\nMissing Skills: " + missingSkills
                + "\nMatch Percentage: " + matchPercentage + "%";
    }

    private Set<String> extractUserSkills(String skills) {

        Set<String> result = new HashSet<>();

        for (String skill : skills.split(",")) {
            String cleanSkill = skill.trim().toLowerCase();

            if (!cleanSkill.isEmpty()) {
                result.add(cleanSkill);
            }
        }

        return result;
    }

    private Set<String> extractRequiredSkills(String description) {

        Set<String> result = new HashSet<>();

        String text = description.toLowerCase();

        String[] commonSkills = {
                "java",
                "python",
                "javascript",
                "html",
                "css",
                "react",
                "react.js",
                "spring",
                "spring boot",
                "sql",
                "mysql",
                "mongodb",
                "django",
                "flask",
                "pyramid",
                "node.js",
                "nodejs",
                "express",
                "angular",
                "git",
                "github",
                "docker",
                "aws",
                "azure",
                "c++",
                "c#"
        };

        for (String skill : commonSkills) {
            if (text.contains(skill)) {
                result.add(skill);
            }
        }

        return result;
    }
}