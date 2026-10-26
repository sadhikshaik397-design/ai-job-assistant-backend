package com.jobassistant.job_assistant.service;

import com.jobassistant.job_assistant.entity.Job;
import com.jobassistant.job_assistant.entity.UserProfile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class JobMatchingService {

    private static final Map<String, Set<String>> ALIASES =
            new LinkedHashMap<>();

    private static final String[] SKILLS = {
            "data structures and algorithms",
            "object oriented programming",
            "software development",
            "backend development",
            "back-end development",
            "cloud technologies",
            "cloud technology",
            "cloud computing",
            "cloud platforms",
            "relational databases",
            "relational database",
            "databases",
            "database",
            "machine learning",
            "web development",
            "python full stack",
            "python fullstack",
            "spring boot",
            "tailwind css",
            "scikit-learn",
            "data analysis",
            "api development",
            "restful apis",
            "restful api",
            "rest apis",
            "rest api",
            "power bi",
            "javascript",
            "typescript",
            "postgresql",
            "tensorflow",
            "elasticsearch",
            "microservices",
            "next.js",
            "node.js",
            "react.js",
            "fastapi",
            "fast api",
            "angular",
            "django",
            "flask",
            "express.js",
            "express",
            "python",
            "java",
            "react",
            "vue.js",
            "vue",
            "html",
            "css",
            "sql",
            "mysql",
            "mongodb",
            "postgres",
            "redis",
            "graphql",
            "openapi",
            "open api",
            "api gateway",
            "serverless",
            "lambda",
            "kubernetes",
            "docker",
            "devops",
            "jenkins",
            "terraform",
            "aws",
            "azure",
            "kafka",
            "rabbitmq",
            "celery",
            "firebase",
            "pandas",
            "numpy",
            "pytorch",
            "git",
            "github",
            "linux",
            "php",
            "ruby",
            "golang",
            "c++",
            "c#",
            "dotnet",
            ".net",
            "oracle",
            "excel",
            "tableau",
            "bootstrap",
            "jquery",
            "vite",
            "json",
            "xml",
            "pytest",
            "unittest",
            "scrum",
            "agile",
            "unit testing",
            "communication",
            "problem solving",
            "algorithms",
            "oop",
            "nodejs",
            "spring"
    };

    private static final Set<String> ROLE_PHRASES =
            Set.of(
                    "full stack",
                    "fullstack",
                    "python full stack",
                    "python fullstack"
            );

    private static final Set<String> NON_SKILL_WORDS =
            Set.of(
                    "the",
                    "and",
                    "for",
                    "with",
                    "from",
                    "this",
                    "that",
                    "you",
                    "your",
                    "our",
                    "are",
                    "will",
                    "have",
                    "has",
                    "who",
                    "what",
                    "how",
                    "all",
                    "any",
                    "job",
                    "role",
                    "work",
                    "working",
                    "years",
                    "year",
                    "experience",
                    "experienced",
                    "required",
                    "requirements",
                    "preferred",
                    "skills",
                    "skill",
                    "developer",
                    "engineer",
                    "candidate",
                    "team",
                    "ability",
                    "knowledge",
                    "strong",
                    "good",
                    "excellent",
                    "responsibilities",
                    "responsibility",
                    "including",
                    "such",
                    "using",
                    "use",
                    "must",
                    "should",
                    "can",
                    "able",
                    "build",
                    "develop",
                    "maintain",
                    "support",
                    "design",
                    "create",
                    "implement",
                    "understanding"
            );

    private static final Pattern SECTION_PATTERN =
            Pattern.compile(
                    "(?i)\\b("
                            + "required skills|"
                            + "requirements|"
                            + "must have|"
                            + "mandatory skills|"
                            + "essential skills|"
                            + "technical requirements|"
                            + "minimum requirements|"
                            + "preferred skills|"
                            + "preferred qualifications|"
                            + "nice to have|"
                            + "good to have|"
                            + "desired skills|"
                            + "bonus skills|"
                            + "optional skills"
                            + ")\\b\\s*[:\\-–]?"
            );

    private static final Pattern SECTION_END_PATTERN =
            Pattern.compile(
                    "(?i)\\b(?:"
                            + "responsibilities|"
                            + "key responsibilities|"
                            + "your responsibilities|"
                            + "about the role|"
                            + "about us|"
                            + "what you will do|"
                            + "what you'll do|"
                            + "what we offer|"
                            + "benefits|"
                            + "salary|"
                            + "compensation|"
                            + "location|"
                            + "equal opportunity|"
                            + "how to apply|"
                            + "application process|"
                            + "company overview"
                            + ")\\b"
            );

    private static final Pattern EXPERIENCE_RANGE_PATTERN =
            Pattern.compile(
                    "(?i)\\b(\\d{1,2})\\s*[-–]\\s*(\\d{1,2})\\s*"
                            + "(?:years?|yrs?)\\b"
            );

    private static final Pattern EXPERIENCE_PATTERN =
            Pattern.compile(
                    "(?i)\\b(\\d{1,2})\\s*\\+?\\s*"
                            + "(?:years?|yrs?)\\b"
            );

    private static final Pattern PROFILE_SKILL_SEPARATOR =
            Pattern.compile("[,;|\\n\\r]+");

    static {
        alias("react", "react.js", "reactjs");
        alias("node.js", "nodejs", "node js");
        alias("javascript", "js", "ecmascript");
        alias("typescript", "ts");
        alias("spring boot", "springboot");
        alias("c++", "cpp");
        alias("c#", "csharp");
        alias("aws", "amazon web services");
        alias("postgresql", "postgres");
        alias("mongodb", "mongo db");
        alias("html", "html5");
        alias("css", "css3");
        alias("github", "git hub");
        alias("tailwind css", "tailwind");
        alias(
                "rest api",
                "restful api",
                "rest apis",
                "restful apis"
        );
        alias(
                "database",
                "databases",
                "relational database",
                "relational databases"
        );
        alias(
                "cloud computing",
                "cloud technology",
                "cloud technologies",
                "cloud platform",
                "cloud platforms"
        );
        alias(
                "backend development",
                "back-end development",
                "back end development"
        );
        alias("machine learning", "ml");
        alias("scikit-learn", "sklearn", "scikit learn");
        alias("power bi", "powerbi");
        alias("object oriented programming", "oop");
        alias("fastapi", "fast api");
        alias("openapi", "open api");
        alias("dotnet", ".net", "asp.net");
        alias("spring", "spring framework");
        alias("express", "express.js");
        alias("vue", "vue.js");
    }

    private static void alias(
            String canonical,
            String... names) {

        Set<String> values = new LinkedHashSet<>();

        values.add(normalize(canonical));

        for (String name : names) {
            values.add(normalize(name));
        }

        ALIASES.put(normalize(canonical), values);
    }

    // ==================================================
    // MAIN MATCH SCORE
    // ==================================================

    public int calculateMatchScore(
            UserProfile profile,
            Job job) {

        if (profile == null || job == null) {
            return 0;
        }

        Set<String> userSkills =
                extractSkills(profile.getSkills());

        Set<String> required =
                getRequiredSkills(job);

        Set<String> preferred =
                getPreferredSkills(job);

        Set<String> jobSkills =
                getJobSkills(job);

        if (userSkills.isEmpty() || jobSkills.isEmpty()) {
            return 0;
        }

        // If the description has no explicit skill sections,
        // treat detected job skills as required skills.
        if (required.isEmpty() && preferred.isEmpty()) {
            required = new LinkedHashSet<>(jobSkills);
        }

        // When a description has a preferred section but no explicit
        // required section, skills found elsewhere in the description or
        // title still provide useful evidence of a match. Otherwise a job
        // could score 0 even when the candidate matches a skill in its title.
        if (required.isEmpty()) {
            double preferredScore =
                    percentage(userSkills, preferred);

            Set<String> otherJobSkills =
                    new LinkedHashSet<>(jobSkills);
            otherJobSkills.removeAll(preferred);

            double skillScore = preferredScore;
            if (!otherJobSkills.isEmpty() && !preferred.isEmpty()) {
                double otherSkillsScore =
                        percentage(userSkills, otherJobSkills);

                skillScore = otherSkillsScore * 0.80
                        + preferredScore * 0.20;
            } else if (!otherJobSkills.isEmpty()) {
                skillScore = percentage(userSkills, otherJobSkills);
            }

            int titleScore =
                    calculateTitleRelevance(profile, job);

            return calculateWeightedScore(
                    skillScore,
                    titleScore,
                    profile,
                    job
            );
        }

        // Required skills make up most of the skills component.
        double requiredScore =
                percentage(userSkills, required);

        double skillScore;
        if (!preferred.isEmpty()) {
            double preferredScore =
                    percentage(userSkills, preferred);

            skillScore = requiredScore * 0.80
                    + preferredScore * 0.20;
        } else {
            skillScore = requiredScore;
        }

        int titleScore =
                calculateTitleRelevance(profile, job);

        return calculateWeightedScore(
                skillScore,
                titleScore,
                profile,
                job
        );
    }

    /**
     * Scores experience explicitly when the job description contains a
     * recognizable years requirement. If no requirement is found, the score
     * uses skills and title only so missing data does not count against the
     * candidate.
     */
    private int calculateWeightedScore(
            double skillScore,
            int titleScore,
            UserProfile profile,
            Job job) {

        int requiredYears = getRequiredExperienceYears(job);

        if (requiredYears <= 0) {
            return clamp((int) Math.round(
                    skillScore * 0.90 + titleScore * 0.10
            ));
        }

        int candidateYears = getCandidateExperienceYears(profile);
        double experienceScore = Math.min(
                100.0,
                candidateYears * 100.0 / requiredYears
        );

        return clamp((int) Math.round(
                skillScore * 0.65
                        + titleScore * 0.10
                        + experienceScore * 0.25
        ));
    }

    // ==================================================
    // MATCHED AND MISSING SKILLS
    // ==================================================

    public Set<String> getMatchedSkills(
            UserProfile profile,
            Job job) {

        return matched(profile, getJobSkills(job));
    }

    public Set<String> getMissingSkills(
            UserProfile profile,
            Job job) {

        return missing(profile, getJobSkills(job));
    }

    public Set<String> getRequiredSkills(Job job) {

        Set<String> required = new LinkedHashSet<>();
        Set<String> preferred = new LinkedHashSet<>();

        extractSkillSections(job, required, preferred);

        /*
         * If no recognized sections exist, use the
         * detected job skills as the requirements.
         */
        if (required.isEmpty() && preferred.isEmpty()) {
            return getJobSkills(job);
        }

        return required;
    }

    public Set<String> getPreferredSkills(Job job) {

        Set<String> required = new LinkedHashSet<>();
        Set<String> preferred = new LinkedHashSet<>();

        extractSkillSections(job, required, preferred);

        return preferred;
    }

    public Set<String> getMatchedRequiredSkills(
            UserProfile profile,
            Job job) {

        return matched(profile, getRequiredSkills(job));
    }

    public Set<String> getMissingRequiredSkills(
            UserProfile profile,
            Job job) {

        return missing(profile, getRequiredSkills(job));
    }

    public Set<String> getMatchedPreferredSkills(
            UserProfile profile,
            Job job) {

        return matched(profile, getPreferredSkills(job));
    }

    public Set<String> getMissingPreferredSkills(
            UserProfile profile,
            Job job) {

        return missing(profile, getPreferredSkills(job));
    }

    // ==================================================
    // RECOMMENDATIONS
    // ==================================================

    public String getRecommendation(int score) {

        if (score >= 80) {
            return "Excellent Match";
        }

        if (score >= 60) {
            return "Good Match";
        }

        if (score >= 40) {
            return "Moderate Match";
        }

        return "Low Match";
    }

    // ==================================================
    // EXPERIENCE
    // ==================================================

    public String getExperienceMatchMessage(
            UserProfile profile,
            Job job) {

        int requiredYears =
                getRequiredExperienceYears(job);

        int candidateYears =
                getCandidateExperienceYears(profile);

        if (requiredYears == 0) {
            return "Experience requirement not identified.";
        }

        if (candidateYears >= requiredYears) {
            return "Experience requirement satisfied.";
        }

        return "Experience gap: job requires "
                + requiredYears
                + " years, while the profile indicates "
                + candidateYears
                + " years.";
    }

    private int getRequiredExperienceYears(Job job) {

        String text = description(job);

        if (text.isBlank()) {
            return 0;
        }

        int maximum = 0;

        // For a range such as "5-7 years", use the lower bound (5) as the
        // minimum requirement. Replace the full range before scanning so the
        // upper bound is not accidentally treated as a separate requirement.
        Matcher rangeMatcher = EXPERIENCE_RANGE_PATTERN.matcher(text);
        StringBuffer normalizedText = new StringBuffer();

        while (rangeMatcher.find()) {
            try {
                int minimum = Integer.parseInt(rangeMatcher.group(1));
                int upper = Integer.parseInt(rangeMatcher.group(2));

                if (minimum <= upper && upper <= 50) {
                    maximum = Math.max(maximum, minimum);
                    rangeMatcher.appendReplacement(
                            normalizedText,
                            Matcher.quoteReplacement(minimum + " years")
                    );
                } else {
                    rangeMatcher.appendReplacement(
                            normalizedText,
                            Matcher.quoteReplacement(rangeMatcher.group())
                    );
                }
            } catch (NumberFormatException ignored) {
                rangeMatcher.appendReplacement(
                        normalizedText,
                        Matcher.quoteReplacement(rangeMatcher.group())
                );
            }
        }
        rangeMatcher.appendTail(normalizedText);

        Matcher matcher = EXPERIENCE_PATTERN.matcher(normalizedText);

        while (matcher.find()) {
            try {
                int years =
                        Integer.parseInt(matcher.group(1));

                if (years <= 50) {
                    maximum = Math.max(maximum, years);
                }
            } catch (NumberFormatException ignored) {
                // Ignore invalid values.
            }
        }

        return maximum;
    }

    private int getCandidateExperienceYears(
            UserProfile profile) {

        if (profile == null || profile.getExperience() == null) {
            return 0;
        }

        String experience =
                normalize(profile.getExperience());

        if (experience.contains("fresher")
                || experience.contains("no experience")
                || experience.contains("entry level")) {
            return 0;
        }

        Matcher matcher =
                EXPERIENCE_PATTERN.matcher(experience);

        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException ignored) {
                return 0;
            }
        }

        return 0;
    }

    // ==================================================
    // JOB SKILL EXTRACTION
    // ==================================================

    private Set<String> getJobSkills(Job job) {

        Set<String> skills = new LinkedHashSet<>();

        if (job == null) {
            return skills;
        }

        addSkills(skills, job.getTitle());
        addSkills(skills, job.getDescription());

        removeOverlappingSkills(skills);

        return skills;
    }

    private void extractSkillSections(
            Job job,
            Set<String> required,
            Set<String> preferred) {

        String text = description(job);

        if (text.isBlank()) {
            return;
        }

        Matcher matcher =
                SECTION_PATTERN.matcher(text);

        int currentStart = -1;
        boolean currentPreferred = false;

        while (matcher.find()) {

            if (currentStart >= 0) {
                String section =
                        text.substring(currentStart, matcher.start());

                addSectionSkills(
                        section,
                        currentPreferred ? preferred : required
                );
            }

            currentStart = matcher.end();

            currentPreferred =
                    isPreferredMarker(matcher.group(1));
        }

        if (currentStart >= 0) {
            String section = text.substring(currentStart);

            addSectionSkills(
                    section,
                    currentPreferred ? preferred : required
            );
        }

        /*
         * A skill appearing in both sections is required.
         */
        preferred.removeAll(required);

        removeOverlappingSkills(required);
        removeOverlappingSkills(preferred);
    }

    private void addSectionSkills(
            String section,
            Set<String> destination) {

        if (section == null || section.isBlank()) {
            return;
        }

        Matcher endMatcher =
                SECTION_END_PATTERN.matcher(section);

        String cleaned = section;

        if (endMatcher.find()) {
            cleaned =
                    section.substring(0, endMatcher.start());
        }

        addSkills(destination, cleaned);
    }

    private boolean isPreferredMarker(String marker) {

        String value = normalize(marker);

        return value.contains("preferred")
                || value.contains("nice to have")
                || value.contains("good to have")
                || value.contains("desired")
                || value.contains("bonus")
                || value.contains("optional");
    }

    private void addSkills(
            Set<String> result,
            String text) {

        if (text == null || text.isBlank()) {
            return;
        }

        String value = normalize(text);

        for (String skill : SKILLS) {

            String normalizedSkill =
                    normalize(skill);

            if (containsWholeSkill(value, normalizedSkill)) {

                String canonical =
                        normalizeSkill(skill);

                if (!ROLE_PHRASES.contains(canonical)) {
                    result.add(canonical);
                }
            }
        }

        removeOverlappingSkills(result);
    }

    private void removeOverlappingSkills(Set<String> skills) {

        /*
         * Remove role labels that aren't individual technical skills.
         */
        skills.removeAll(ROLE_PHRASES);

        /*
         * Spring Boot includes Spring; avoid double-counting both.
         */
        if (skills.contains("spring boot")) {
            skills.remove("spring");
        }

        /*
         * React.js and React normalize to the same canonical skill.
         * Python and its frameworks remain distinct skills.
         */
    }

    // ==================================================
    // SKILL NORMALIZATION AND COMPARISON
    // ==================================================

    private Set<String> extractSkills(String value) {

        Set<String> result = new LinkedHashSet<>();

        if (value == null || value.isBlank()) {
            return result;
        }

        /*
         * Supports the profile format:
         * Java, HTML, CSS, JavaScript, React.js, Python
         */
        for (String skill :
                PROFILE_SKILL_SEPARATOR.split(value)) {

            String normalized =
                    normalizeSkill(skill);

            if (!normalized.isBlank()) {
                result.add(normalized);
            }
        }

        return result;
    }

    private Set<String> matched(
            UserProfile profile,
            Set<String> skills) {

        Set<String> result = new LinkedHashSet<>();

        if (profile == null || skills == null) {
            return result;
        }

        Set<String> userSkills =
                extractSkills(profile.getSkills());

        for (String skill : skills) {
            if (hasSkill(userSkills, skill)) {
                result.add(skill);
            }
        }

        return result;
    }

    private Set<String> missing(
            UserProfile profile,
            Set<String> skills) {

        Set<String> result = new LinkedHashSet<>();

        if (skills == null) {
            return result;
        }

        result.addAll(skills);
        result.removeAll(matched(profile, skills));

        return result;
    }

    private int percentage(
            Set<String> user,
            Set<String> job) {

        if (job == null || job.isEmpty()) {
            return 0;
        }

        int matches = 0;

        for (String skill : job) {
            if (hasSkill(user, skill)) {
                matches++;
            }
        }

        return (int) Math.round(
                matches * 100.0 / job.size()
        );
    }

    private boolean hasSkill(
            Set<String> userSkills,
            String jobSkill) {

        if (userSkills == null || jobSkill == null) {
            return false;
        }

        String required =
                normalizeSkill(jobSkill);

        for (String user : userSkills) {

            String candidate =
                    normalizeSkill(user);

            if (candidate.equals(required)) {
                return true;
            }

            Set<String> aliases =
                    ALIASES.get(required);

            if (aliases != null && aliases.contains(candidate)) {
                return true;
            }

            aliases = ALIASES.get(candidate);

            if (aliases != null && aliases.contains(required)) {
                return true;
            }
        }

        return false;
    }

    private String normalizeSkill(String skill) {

        String value = normalize(skill);

        for (Map.Entry<String, Set<String>> entry :
                ALIASES.entrySet()) {

            if (entry.getValue().contains(value)) {
                return entry.getKey();
            }
        }

        return value;
    }

    private boolean containsWholeSkill(
            String text,
            String skill) {

        /*
         * Allow punctuation in skill names such as:
         * C++, C#, .NET, Node.js and React.js.
         */
        String regex =
                "(?<![a-z0-9])"
                        + Pattern.quote(skill)
                        + "(?![a-z0-9])";

        return Pattern.compile(regex)
                .matcher(text)
                .find();
    }

    // ==================================================
    // TITLE RELEVANCE
    // ==================================================

    private int calculateTitleRelevance(
            UserProfile profile,
            Job job) {

        if (profile == null
                || job == null
                || profile.getPreferredJobTitle() == null
                || job.getTitle() == null) {
            return 0;
        }

        Set<String> preferredWords =
                meaningfulWords(profile.getPreferredJobTitle());

        Set<String> jobWords =
                meaningfulWords(job.getTitle());

        if (preferredWords.isEmpty() || jobWords.isEmpty()) {
            return 0;
        }

        int matches = 0;

        for (String word : preferredWords) {
            if (jobWords.contains(word)) {
                matches++;
            }
        }

        return (int) Math.round(
                matches * 100.0 / preferredWords.size()
        );
    }

    private Set<String> meaningfulWords(String value) {

        Set<String> result = new LinkedHashSet<>();

        if (value == null) {
            return result;
        }

        List<String> words = new ArrayList<>(
                Arrays.asList(
                        normalize(value)
                                .replaceAll("[^a-z0-9+#.]+", " ")
                                .split("\\s+")
                )
        );

        for (String word : words) {

            if (word.length() > 1
                    && !NON_SKILL_WORDS.contains(word)) {
                result.add(word);
            }
        }

        return result;
    }

    // ==================================================
    // UTILITIES
    // ==================================================

    private int clamp(int score) {
        return Math.max(0, Math.min(100, score));
    }

    private String description(Job job) {

        if (job == null || job.getDescription() == null) {
            return "";
        }

        return normalize(job.getDescription());
    }

    private static String normalize(String value) {

        if (value == null) {
            return "";
        }

        return value.toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ")
                .trim();
    }
}
