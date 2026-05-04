package com.sijin.codereview.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyzeResponse {

    private String jobId;
    private String status;
    private String message;

    private Integer score;
    private String grade;
    private String summary;
}