package com.sijin.codereview.analysis;

import com.sijin.codereview.model.CodeIssue;
import com.sijin.codereview.util.CodeSnippetUtil;
import com.sijin.codereview.util.FileReaderUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LongMethodAnalyzer {

    private static final int MAX_METHOD_LINES = 50;

    public static List<CodeIssue> analyze(File file) {

        List<CodeIssue> issues = new ArrayList<>();

        if (!file.getName().endsWith(".java")) {
            return issues;
        }

        List<String> lines = FileReaderUtil.readLines(file);

        boolean inMethod = false;
        int methodStartLine = -1;
        int methodLineCount = 0;
        int braceDepth = 0;
        String methodName = "unknown";

        for (int i = 0; i < lines.size(); i++) {

            String line = lines.get(i);
            String trimmed = line.trim();

            // 🔹 Detect method start
            if (!inMethod && looksLikeMethodStart(trimmed)) {

                inMethod = true;
                methodStartLine = i + 1;
                methodLineCount = 1;
                methodName = extractMethodName(trimmed);

                braceDepth = countChar(trimmed, '{') - countChar(trimmed, '}');

                if (braceDepth <= 0) {
                    inMethod = false;
                    methodStartLine = -1;
                    methodLineCount = 0;
                }

                continue;
            }

            // 🔹 Inside method
            if (inMethod) {

                methodLineCount++;

                braceDepth += countChar(line, '{');
                braceDepth -= countChar(line, '}');

                // 🔹 Method ends
                if (braceDepth <= 0) {

                    if (methodLineCount > MAX_METHOD_LINES) {

                        CodeIssue issue = new CodeIssue();

                        issue.setType("LONG_METHOD");
                        issue.setSeverity("MEDIUM");
                        issue.setSeverityScore(2);

                        issue.setFile(file.getName());
                        issue.setLine(methodStartLine);

                        issue.setMessage("Method '" + methodName + "' is too long: " + methodLineCount + " lines");
                        issue.setSuggestion("Break this method into smaller helper methods.");

                        // 🔥 Day 7 additions
                        issue.setCodeSnippet(CodeSnippetUtil.extractSnippet(file, methodStartLine));
                        issue.setContext("Java method in " + file.getName());

                        issue.setAiExplanation(null);
                        issue.setAiSuggestion(null);

                        issues.add(issue);
                    }

                    inMethod = false;
                    methodStartLine = -1;
                    methodLineCount = 0;
                }
            }
        }

        return issues;
    }

    private static boolean looksLikeMethodStart(String line) {

        if (line.isBlank()) return false;

        if (line.startsWith("//") || line.startsWith("*") || line.startsWith("@")) return false;

        if (line.startsWith("if ") || line.startsWith("if(") ||
                line.startsWith("for ") || line.startsWith("for(") ||
                line.startsWith("while ") || line.startsWith("while(") ||
                line.startsWith("switch ") || line.startsWith("switch(") ||
                line.startsWith("catch ") || line.startsWith("catch(") ||
                line.startsWith("else if")) {
            return false;
        }

        if (line.contains(" class ") || line.startsWith("class ") ||
                line.contains(" interface ") || line.startsWith("interface ") ||
                line.contains(" enum ") || line.startsWith("enum ")) {
            return false;
        }

        return line.contains("(") && line.contains(")") && line.contains("{") && !line.endsWith(";");
    }

    private static String extractMethodName(String line) {

        int parenIndex = line.indexOf('(');
        if (parenIndex <= 0) return "unknown";

        String beforeParen = line.substring(0, parenIndex).trim();
        String[] tokens = beforeParen.split("\\s+");

        if (tokens.length == 0) return "unknown";

        return tokens[tokens.length - 1].replaceAll("[^A-Za-z0-9_$]", "");
    }

    private static int countChar(String text, char target) {

        int count = 0;

        for (char c : text.toCharArray()) {
            if (c == target) count++;
        }

        return count;
    }
}