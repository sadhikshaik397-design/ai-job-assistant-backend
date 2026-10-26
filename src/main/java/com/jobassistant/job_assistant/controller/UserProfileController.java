package com.jobassistant.job_assistant.controller;

import com.jobassistant.job_assistant.config.AuthTokenInterceptor;
import com.jobassistant.job_assistant.entity.UserProfile;
import com.jobassistant.job_assistant.repository.UserProfileRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profile")
public class UserProfileController {
    private final UserProfileRepository repository;

    public UserProfileController(UserProfileRepository repository) {
        this.repository = repository;
    }

    private Long currentUserId(HttpServletRequest request) {
        return (Long) request.getAttribute(AuthTokenInterceptor.USER_ID_ATTRIBUTE);
    }

    @GetMapping
    public List<UserProfile> getMyProfiles(HttpServletRequest request) {
        return repository.findAllByUserId(currentUserId(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfile> getMyProfile(HttpServletRequest request) {
        return repository.findFirstByUserIdOrderByIdAsc(currentUserId(request))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserProfile> createProfile(
            @RequestBody UserProfile profile,
            HttpServletRequest request) {
        profile.setId(null);
        profile.setUserId(currentUserId(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(profile));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfile> getProfileById(
            @PathVariable Long id, HttpServletRequest request) {
        return repository.findByIdAndUserId(id, currentUserId(request))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserProfile> updateProfile(
            @PathVariable Long id,
            @RequestBody UserProfile updated,
            HttpServletRequest request) {
        return repository.findByIdAndUserId(id, currentUserId(request))
                .map(profile -> {
                    profile.setName(updated.getName());
                    profile.setEducation(updated.getEducation());
                    profile.setSkills(updated.getSkills());
                    profile.setExperience(updated.getExperience());
                    profile.setPreferredLocation(updated.getPreferredLocation());
                    profile.setPreferredJobTitle(updated.getPreferredJobTitle());
                    return ResponseEntity.ok(repository.save(profile));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfile(
            @PathVariable Long id, HttpServletRequest request) {
        return repository.findByIdAndUserId(id, currentUserId(request))
                .map(profile -> {
                    repository.delete(profile);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
