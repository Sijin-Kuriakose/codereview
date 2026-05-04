package com.sijin.codereview.service;

import com.sijin.codereview.analysis.*;
import com.sijin.codereview.dto.AnalysisResult;
import com.sijin.codereview.model.CodeIssue;
import com.sijin.codereview.util.FileScannerUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CodeAnalysisService {

    private final GeminiService geminiService;

    public AnalysisResult analyzeRepository(String repoPath) {

        System.out.println("📂 Scanning files from: " + repoPath);

        List<File> codeFiles = FileScannerUtil.getCodeFiles(repoPath);
        List<CodeIssue> rawIssues = new ArrayList<>();

        // 🔥 STEP 0: RUN ANALYZERS
        for (File file : codeFiles) {
            rawIssues.addAll(TodoCommentAnalyzer.analyze(file));
            rawIssues.addAll(FileSizeAnalyzer.analyze(file));
            rawIssues.addAll(LongMethodAnalyzer.analyze(file));
            rawIssues.addAll(NestingComplexityAnalyzer.analyze(file));
            rawIssues.addAll(HardcodedSecretAnalyzer.analyze(file));
        }

        System.out.println("🔍 Raw issues found: " + rawIssues.size());

        // 🔥 STEP 1: REMOVE DUPLICATES
        List<CodeIssue> uniqueIssues = rawIssues.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                i -> i.getType() + "|" + i.getFile() + "|" + i.getLine(),
                                i -> i,
                                (a, b) -> a
                        ),
                        map -> new ArrayList<>(map.values())
                ));

        System.out.println("✅ Unique issues: " + uniqueIssues.size());

        // 🔥 STEP 2: LIMIT PER FILE
        Map<String, List<CodeIssue>> grouped = uniqueIssues.stream()
                .collect(Collectors.groupingBy(i -> i.getFile() + "|" + i.getType()));

        List<CodeIssue> perFileLimited = new ArrayList<>();

        for (List<CodeIssue> group : grouped.values()) {
            group.sort(Comparator.comparingInt(CodeIssue::getSeverityScore).reversed());
            perFileLimited.add(group.get(0));
        }

        // 🔥 STEP 3: GLOBAL LIMIT FOR NESTED
        List<CodeIssue> nestedIssues = perFileLimited.stream()
                .filter(i -> "NESTED_COMPLEXITY".equals(i.getType()))
                .sorted(Comparator.comparingInt(CodeIssue::getSeverityScore).reversed())
                .limit(5)
                .toList();

        List<CodeIssue> otherIssues = perFileLimited.stream()
                .filter(i -> !"NESTED_COMPLEXITY".equals(i.getType()))
                .toList();

        List<CodeIssue> finalIssues = new ArrayList<>();
        finalIssues.addAll(nestedIssues);
        finalIssues.addAll(otherIssues);

        // 🔥 STEP 4: FINAL SORT
        finalIssues.sort(
                Comparator.comparingInt(CodeIssue::getSeverityScore).reversed()
                        .thenComparing(CodeIssue::getFile)
        );

        System.out.println("📊 Final issues: " + finalIssues.size());

        // 🔥 STEP 5: AI (LIMITED + PRIORITIZED)
        int aiLimit = 1;
        int count = 0;

        for (CodeIssue issue : finalIssues) {

            if (count >= aiLimit) break;
            if (issue == null) continue;
            if ("LARGE_FILE".equals(issue.getType())) continue;

            try {
                System.out.println("🤖 AI processing: " + issue.getType() + " | Severity: " + issue.getSeverity());
                geminiService.enrichIssue(issue);
                count++;
            } catch (Exception e) {
                System.out.println("❌ AI skipped for: " + issue.getFile());
            }
        }

        System.out.println("🤖 AI processed: " + count);

        // 🔥 STEP 6: COUNT
        int highCount = (int) finalIssues.stream()
                .filter(i -> "HIGH".equalsIgnoreCase(i.getSeverity()))
                .count();

        int mediumCount = (int) finalIssues.stream()
                .filter(i -> "MEDIUM".equalsIgnoreCase(i.getSeverity()))
                .count();

        int lowCount = (int) finalIssues.stream()
                .filter(i -> "LOW".equalsIgnoreCase(i.getSeverity()))
                .count();

        // 🔥 STEP 7: GROUP
        Map<String, Long> issuesByType = finalIssues.stream()
                .collect(Collectors.groupingBy(
                        CodeIssue::getType,
                        LinkedHashMap::new,
                        Collectors.counting()
                ));

        // 🔥 STEP 8: SCORE CALCULATION (THIS WAS MISSING)
        int score = calculateScore(highCount, mediumCount, lowCount);
        String grade = calculateGrade(score);
        String summary = buildSummary(
                codeFiles.size(),
                finalIssues.size(),
                highCount,
                mediumCount,
                lowCount,
                score,
                grade
        );

        System.out.println("🎯 Analysis finished");

        return new AnalysisResult(
                codeFiles.size(),
                finalIssues.size(),
                highCount,
                mediumCount,
                lowCount,
                issuesByType,
                finalIssues,
                score,
                grade,
                summary
        );
    }

    // 🔥 SCORE LOGIC
    private int calculateScore(int high, int medium, int low) {
        int score = 100;
        score -= high * 10;
        score -= medium * 5;
        score -= low * 2;
        return Math.max(score, 0);
    }

    private String calculateGrade(int score) {
        if (score >= 90) return "A";
        if (score >= 75) return "B";
        if (score >= 60) return "C";
        if (score >= 40) return "D";
        return "F";
    }

    private String buildSummary(
            int scannedFiles,
            int totalIssues,
            int high,
            int medium,
            int low,
            int score,
            String grade
    ) {
        return String.format(
                "Scanned %d files and found %d issues (%d high, %d medium, %d low). Score: %d (%s). %s",
                scannedFiles,
                totalIssues,
                high,
                medium,
                low,
                score,
                grade,
                qualityMessage(score)
        );
    }

    private String qualityMessage(int score) {
        if (score >= 90) return "Excellent code quality.";
        if (score >= 75) return "Good code quality.";
        if (score >= 60) return "Moderate code quality.";
        if (score >= 40) return "Poor code quality.";
        return "Very poor code quality.";
    }
}