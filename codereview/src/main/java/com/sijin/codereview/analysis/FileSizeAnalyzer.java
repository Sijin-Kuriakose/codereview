package com.sijin.codereview.analysis;

import com.sijin.codereview.model.CodeIssue;
import com.sijin.codereview.util.CodeSnippetUtil;
import com.sijin.codereview.util.FileReaderUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileSizeAnalyzer {

    private static final int MAX_LINES = 300;

    public static List<CodeIssue> analyze(File file) {

        List<CodeIssue> issues = new ArrayList<>();
        List<String> lines = FileReaderUtil.readLines(file);

        if (lines.size() > MAX_LINES) {

            CodeIssue issue = new CodeIssue();

            issue.setType("LARGE_FILE");
            issue.setSeverity("MEDIUM");
            issue.setSeverityScore(2);

            issue.setFile(file.getName());
            issue.setLine(-1);

            issue.setMessage("File has too many lines: " + lines.size());
            issue.setSuggestion("Break this file into smaller classes/modules.");

            issue.setCodeSnippet("");
            issue.setContext("Large file detected: " + file.getName());

            issue.setAiExplanation(null);
            issue.setAiSuggestion(null);

            issues.add(issue);
        }

        return issues;
    }
}