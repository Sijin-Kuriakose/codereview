package com.sijin.codereview.dto;

import com.sijin.codereview.model.CodeIssue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisResult {

    private int scannedFiles;
    private int totalIssues;

    private int highCount;
    private int mediumCount;
    private int lowCount;

    private Map<String, Long> issuesByType;
    private List<CodeIssue> issues;

    private int score;
    private String grade;
    private String summary;
}