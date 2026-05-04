package com.sijin.codereview.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class GitUtil {

    public static String cloneRepository(String repoUrl, String jobId) throws IOException, InterruptedException {

        String baseDir = System.getProperty("java.io.tmpdir");
        String repoPath = baseDir + File.separator + "repo-" + jobId;

        File dir = new File(repoPath);

        if (dir.exists()) {
            deleteDirectory(dir);
        }

        dir.mkdirs();

        ProcessBuilder processBuilder = new ProcessBuilder();

        // 🔥 KEY FIXES HERE
        processBuilder.command(
                "git",
                "clone",
                "--depth", "1",          // shallow clone (BIG SPEED BOOST)
                "--single-branch",       // avoid extra branches
                repoUrl,
                repoPath
        );

        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        // 🔥 PRINT OUTPUT (so you know what’s happening)
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[GIT] " + line);
            }
        }

        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Git clone failed. Exit code: " + exitCode);
        }

        return repoPath;
    }
    // 🔥 Utility to delete existing folder
    private static void deleteDirectory(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDirectory(f);
            }
        }
        file.delete();
    }
}