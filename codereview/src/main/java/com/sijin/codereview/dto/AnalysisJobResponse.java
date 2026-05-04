package com.sijin.codereview.dto;

import com.sijin.codereview.model.AnalysisJob;
import com.sijin.codereview.model.AnalysisStatus;
import com.sijin.codereview.model.CodeIssue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisJobResponse {

    private String id;
    private String repoUrl;
    private String repoName;
    private AnalysisStatus status;
    private String message;
    private String localPath;

    private int scannedFiles;
    private int totalIssues;

    private int highCount;
    private int mediumCount;
    private int lowCount;

    private int score;
    private String grade;

    private String analysisSummary;
    private Map<String, Long> issuesByType;
    private List<CodeIssue> issues;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AnalysisJobResponse from(AnalysisJob job) {
        return AnalysisJobResponse.builder()
                .id(job.getId())
                .repoUrl(job.getRepoUrl())
                .repoName(job.getRepoName())
                .status(job.getStatus())
                .message(job.getMessage())
                .localPath(job.getLocalPath())
                .scannedFiles(job.getScannedFiles())
                .totalIssues(job.getTotalIssues())
                .highCount(job.getHighCount())
                .mediumCount(job.getMediumCount())
                .lowCount(job.getLowCount())
                .score(job.getScore())
                .grade(job.getGrade())
                .analysisSummary(job.getAnalysisSummary())
                .issuesByType(job.getIssuesByType())
                .issues(job.getIssues())
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }
}