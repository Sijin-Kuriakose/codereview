package com.sijin.codereview.analysis;

import com.sijin.codereview.model.CodeIssue;
import com.sijin.codereview.util.CodeSnippetUtil;
import com.sijin.codereview.util.FileReaderUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HardcodedSecretAnalyzer {

    public static List<CodeIssue> analyze(File file) {

        List<CodeIssue> issues = new ArrayList<>();

        if (!file.getName().endsWith(".java")) {
            return issues;
        }

        List<String> lines = FileReaderUtil.readLines(file);

        for (int i = 0; i < lines.size(); i++) {

            String line = lines.get(i).trim();
            String lower = line.toLowerCase();

            // skip comments
            if (line.startsWith("//") || line.startsWith("*")) {
                continue;
            }

            // skip environment-based usage
            if (lower.contains("getenv") ||
                    lower.contains("system.getenv") ||
                    lower.contains("system.getproperty")) {
                continue;
            }

            if (looksLikeSecretAssignment(lower, line)) {

                CodeIssue issue = new CodeIssue();

                issue.setType("HARDCODED_SECRET");
                issue.setSeverity("HIGH");
                issue.setSeverityScore(3);

                issue.setFile(file.getName());
                issue.setLine(i + 1);

                issue.setMessage("Possible hardcoded secret found");
                issue.setSuggestion("Move secrets to environment variables or a secret manager.");

                // 🔥 Day 7 additions
                issue.setCodeSnippet(CodeSnippetUtil.extractSnippet(file, i + 1));
                issue.setContext("Potential hardcoded secret in " + file.getName());

                issue.setAiExplanation(null);
                issue.setAiSuggestion(null);

                issues.add(issue);
            }
        }

        return issues;
    }

    private static boolean looksLikeSecretAssignment(String lower, String originalLine) {

        boolean keyMention =
                lower.contains("password") ||
                        lower.contains("passwd") ||
                        lower.contains("secret") ||
                        lower.contains("apikey") ||
                        lower.contains("api_key") ||
                        lower.contains("token") ||
                        lower.contains("accesskey") ||
                        lower.contains("access_key") ||
                        lower.contains("clientsecret") ||
                        lower.contains("client_secret");

        boolean hasAssignment =
                originalLine.contains("=") || originalLine.contains(":");

        boolean hasLiteral =
                originalLine.contains("\"") || originalLine.contains("'");

        return keyMention && hasAssignment && hasLiteral;
    }
}