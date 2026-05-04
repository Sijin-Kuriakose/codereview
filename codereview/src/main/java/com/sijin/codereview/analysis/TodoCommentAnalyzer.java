package com.sijin.codereview.analysis;

import com.sijin.codereview.model.CodeIssue;
import com.sijin.codereview.util.CodeSnippetUtil;
import com.sijin.codereview.util.FileReaderUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TodoCommentAnalyzer {

    public static List<CodeIssue> analyze(File file) {

        List<CodeIssue> issues = new ArrayList<>();
        List<String> lines = FileReaderUtil.readLines(file);

        for (int i = 0; i < lines.size(); i++) {

            String line = lines.get(i).toLowerCase();

            if (line.contains("todo") || line.contains("fixme")) {

                CodeIssue issue = new CodeIssue();

                issue.setType("TODO_COMMENT");
                issue.setSeverity("LOW");
                issue.setSeverityScore(1);

                issue.setFile(file.getName());
                issue.setLine(i + 1);

                issue.setMessage("TODO/FIXME comment found");
                issue.setSuggestion("Remove or complete the TODO before production.");

                issue.setCodeSnippet(CodeSnippetUtil.extractSnippet(file, i + 1));
                issue.setContext("TODO comment in " + file.getName());

                issue.setAiExplanation(null);
                issue.setAiSuggestion(null);

                issues.add(issue);
            }
        }

        return issues;
    }
}