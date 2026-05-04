package com.sijin.codereview.analysis;

import com.sijin.codereview.model.CodeIssue;
import com.sijin.codereview.util.CodeSnippetUtil;
import com.sijin.codereview.util.FileReaderUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NestingComplexityAnalyzer {

    public static List<CodeIssue> analyze(File file) {

        List<CodeIssue> issues = new ArrayList<>();

        if (!file.getName().endsWith(".java")) {
            return issues;
        }

        List<String> lines = FileReaderUtil.readLines(file);
        int nestingDepth = 0;

        for (int i = 0; i < lines.size(); i++) {

            String line = lines.get(i);
            String trimmed = line.trim();

            if (isControlStatementStart(trimmed)) {

                String severity;
                int score;

                if (nestingDepth >= 4) {
                    severity = "HIGH";
                    score = 3;
                } else if (nestingDepth >= 2) {
                    severity = "MEDIUM";
                    score = 2;
                } else {
                    severity = null;
                    score = 0;
                }

                if (severity != null) {

                    CodeIssue issue = new CodeIssue();

                    issue.setType("NESTED_COMPLEXITY");
                    issue.setSeverity(severity);
                    issue.setSeverityScore(score);

                    issue.setFile(file.getName());
                    issue.setLine(i + 1);

                    issue.setMessage("Deep nesting level " + nestingDepth + " at line " + (i + 1));
                    issue.setSuggestion("Reduce nesting using guard clauses, early returns, or smaller helper methods.");

                    // 🔥 Day 7 additions
                    issue.setCodeSnippet(CodeSnippetUtil.extractSnippet(file, i + 1));
                    issue.setContext("Nested control structure in " + file.getName());

                    issue.setAiExplanation(null);
                    issue.setAiSuggestion(null);

                    issues.add(issue);
                }
            }

            nestingDepth += countChar(line, '{');
            nestingDepth -= countChar(line, '}');

            if (nestingDepth < 0) {
                nestingDepth = 0;
            }
        }

        return issues;
    }

    private static boolean isControlStatementStart(String line) {

        if (line.isBlank()) {
            return false;
        }

        return line.startsWith("if ")
                || line.startsWith("if(")
                || line.startsWith("for ")
                || line.startsWith("for(")
                || line.startsWith("while ")
                || line.startsWith("while(")
                || line.startsWith("switch ")
                || line.startsWith("switch(")
                || line.startsWith("try")
                || line.startsWith("catch ")
                || line.startsWith("catch(")
                || line.startsWith("else if");
    }

    private static int countChar(String text, char target) {

        int count = 0;

        for (char c : text.toCharArray()) {
            if (c == target) {
                count++;
            }
        }

        return count;
    }
}