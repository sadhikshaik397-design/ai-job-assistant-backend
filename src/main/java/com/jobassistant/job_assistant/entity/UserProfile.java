package com.jobassistant.job_assistant.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String education;
    private String skills;
    private String experience;
    private String preferredLocation;
    private String preferredJobTitle;

    // Added for per-account ownership. Existing rows need a one-time migration.
    private Long userId;

    public UserProfile() {
    }

    public UserProfile(
            String name,
            String education,
            String skills,
            String experience,
            String preferredLocation,
            String preferredJobTitle) {

        this.name = name;
        this.education = education;
        this.skills = skills;
        this.experience = experience;
        this.preferredLocation = preferredLocation;
        this.preferredJobTitle = preferredJobTitle;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getPreferredLocation() {
        return preferredLocation;
    }

    public void setPreferredLocation(String preferredLocation) {
        this.preferredLocation = preferredLocation;
    }

    public String getPreferredJobTitle() {
        return preferredJobTitle;
    }

    public void setPreferredJobTitle(String preferredJobTitle) {
        this.preferredJobTitle = preferredJobTitle;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
