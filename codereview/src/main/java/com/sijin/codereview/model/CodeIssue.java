package com.sijin.codereview.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CodeIssue {

    private String type;
    private String severity;
    private int severityScore;

    private String file;
    private int line;

    private String message;
    private String suggestion;

    // 🔥 Day 7 additions
    private String codeSnippet;
    private String context;

    private String aiExplanation;
    private String aiSuggestion;
}