package com.sijin.codereview.util;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

public class FileReaderUtil {

    public static List<String> readLines(File file) {
        try {
            return Files.readAllLines(file.toPath());
        } catch (Exception e) {
            throw new RuntimeException("Error reading file: " + file.getName(), e);
        }
    }
}