package com.sijin.codereview.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "analysis_jobs")
public class AnalysisJob {

    @Id
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
}