package com.sijin.codereview.service;

import com.sijin.codereview.dto.AnalysisResult;
import com.sijin.codereview.dto.AnalyzeRequest;
import com.sijin.codereview.model.AnalysisJob;
import com.sijin.codereview.model.AnalysisStatus;
import com.sijin.codereview.repository.AnalysisJobRepository;
import com.sijin.codereview.util.GitUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final AnalysisJobRepository analysisJobRepository;
    private final CodeAnalysisService codeAnalysisService;

    public AnalysisJob createJob(AnalyzeRequest request) {

        validateRepoUrl(request.getRepoUrl());

        AnalysisJob job = new AnalysisJob();
        job.setRepoUrl(request.getRepoUrl());
        job.setRepoName(extractRepoName(request.getRepoUrl()));
        job.setStatus(AnalysisStatus.PENDING);
        job.setMessage("Job created");
        job.setCreatedAt(LocalDateTime.now());
        job.setUpdatedAt(LocalDateTime.now());

        return analysisJobRepository.save(job);
    }

    @Async
    public void processJob(String jobId) {

        AnalysisJob job = analysisJobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found: " + jobId));

        try {
            // 🔥 STEP 1: START
            job.setStatus(AnalysisStatus.RUNNING);
            job.setMessage("Cloning repository...");
            job.setUpdatedAt(LocalDateTime.now());
            analysisJobRepository.save(job);

            System.out.println("🚀 Cloning started for job: " + jobId);

            // 🔥 STEP 2: CLONE
            String localPath = GitUtil.cloneRepository(job.getRepoUrl(), job.getId());

            System.out.println("✅ Clone completed: " + localPath);

            job.setLocalPath(localPath);

            // 🔥 STEP 3: ANALYSIS START
            job.setMessage("Analyzing code...");
            job.setUpdatedAt(LocalDateTime.now());
            analysisJobRepository.save(job);

            System.out.println("🔍 Analysis started...");

            // 🔥 STEP 4: RUN ANALYSIS
            AnalysisResult result = codeAnalysisService.analyzeRepository(localPath);

            System.out.println("✅ Analysis completed");

            // 🔥 STEP 5: SAVE RESULTS
            job.setScannedFiles(result.getScannedFiles());
            job.setTotalIssues(result.getTotalIssues());
            job.setHighCount(result.getHighCount());
            job.setMediumCount(result.getMediumCount());
            job.setLowCount(result.getLowCount());
            job.setIssuesByType(result.getIssuesByType());
            job.setIssues(result.getIssues());

            // ✅ 🔥 THIS WAS MISSING (CRITICAL)
            job.setScore(result.getScore());
            job.setGrade(result.getGrade());

            // ✅ 🔥 USE NEW SUMMARY (DO NOT OVERRIDE)
            job.setAnalysisSummary(result.getSummary());

            // 🔥 STEP 6: COMPLETE
            job.setStatus(AnalysisStatus.COMPLETED);
            job.setMessage("Repository analyzed successfully");

            System.out.println("🎯 Job COMPLETED: " + jobId);
            System.out.println("📊 Score: " + result.getScore() + " | Grade: " + result.getGrade());

        } catch (Exception e) {

            e.printStackTrace();

            System.out.println("❌ Job FAILED: " + jobId);

            job.setStatus(AnalysisStatus.FAILED);
            job.setMessage("Error: " + e.getMessage());
        }

        job.setUpdatedAt(LocalDateTime.now());
        analysisJobRepository.save(job);
    }

    public AnalysisJob getJobById(String id) {
        return analysisJobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found: " + id));
    }

    private void validateRepoUrl(String repoUrl) {
        if (repoUrl == null || repoUrl.isBlank()) {
            throw new IllegalArgumentException("Repo URL cannot be empty");
        }
        if (!repoUrl.startsWith("https://github.com/")) {
            throw new IllegalArgumentException("Only GitHub repo URLs are allowed");
        }
    }

    private String extractRepoName(String repoUrl) {
        String[] parts = repoUrl.split("/");
        if (parts.length < 5) {
            throw new IllegalArgumentException("Invalid GitHub repo URL");
        }
        return parts[parts.length - 1];
    }
}