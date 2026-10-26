package com.jobassistant.job_assistant.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AIService {

    private final ChatClient chatClient;

    public AIService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String analyzeJob(String jobDescription) {

        return chatClient
                .prompt()
                .user("""
                        You are a job description analyzer.

                        Read the job description carefully and extract information
                        ONLY from the text provided.

                        Do not invent or assume anything.

                        Return EXACTLY this format:

                        1. Required Skills:
                        - skill 1
                        - skill 2
                        - skill 3

                        2. Important Technologies:
                        - technology 1
                        - technology 2
                        - technology 3

                        3. Experience Requirements:
                        - experience requirement

                        4. Short Summary:
                        - Write 2 short sentences explaining the job.

                        RULES:
                        - Extract programming languages such as Java, Python, JavaScript.
                        - Extract frameworks such as Spring Boot, React, Django.
                        - Extract databases such as SQL, MySQL, MongoDB.
                        - Extract cloud/tools such as AWS, Docker, Git.
                        - Extract explicit experience requirements such as
                          "2 years experience" or "freshers".
                        - Do not add information that is not present.
                        - If a section has no information, write:
                          - Not specified
                        - Keep the answer concise.

                        JOB DESCRIPTION:
                        """ + jobDescription)
                .call()
                .content();
    }
}