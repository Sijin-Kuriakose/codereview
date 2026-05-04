package com.sijin.codereview.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FileScannerUtil {

    private static final Set<String> IGNORED_DIRS = Set.of(
            ".git", "node_modules", "target", "build", "dist", ".idea", "out"
    );

    public static List<File> getCodeFiles(String rootPath) {
        List<File> files = new ArrayList<>();
        scan(new File(rootPath), files);
        return files;
    }

    private static void scan(File file, List<File> files) {
        if (file == null || !file.exists()) {
            return;
        }

        if (file.isDirectory()) {
            if (IGNORED_DIRS.contains(file.getName())) {
                return;
            }

            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    scan(child, files);
                }
            }
        } else {
            if (isCodeFile(file.getName())) {
                files.add(file);
            }
        }
    }

    private static boolean isCodeFile(String name) {
        return name.endsWith(".java")
                || name.endsWith(".py")
                || name.endsWith(".js")
                || name.endsWith(".ts")
                || name.endsWith(".html")
                || name.endsWith(".css");
    }
}