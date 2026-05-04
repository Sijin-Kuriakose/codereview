package com.sijin.codereview.util;

import java.io.File;
import java.util.List;

public class CodeSnippetUtil {

    public static String extractSnippet(File file, int lineNumber) {

        List<String> lines = FileReaderUtil.readLines(file);

        if (lineNumber <= 0 || lineNumber > lines.size()) {
            return "";
        }

        int start = Math.max(0, lineNumber - 3);
        int end = Math.min(lines.size(), lineNumber + 2);

        StringBuilder snippet = new StringBuilder();

        for (int i = start; i < end; i++) {
            snippet.append(lines.get(i)).append("\n");
        }

        return snippet.toString();
    }
}