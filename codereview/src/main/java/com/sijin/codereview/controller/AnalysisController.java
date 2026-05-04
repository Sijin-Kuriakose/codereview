package com.sijin.codereview.controller;

import com.sijin.codereview.dto.AnalysisJobResponse;
import com.sijin.codereview.dto.AnalyzeRequest;
import com.sijin.codereview.dto.AnalyzeResponse;
import com.sijin.codereview.model.AnalysisJob;
import com.sijin.codereview.service.AnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin // 🔥 allows frontend to call API
public class AnalysisController {

    private final AnalysisService analysisService;

    // 🚀 START ANALYSIS
    @PostMapping("/analyze")
    public AnalyzeResponse analyze(@RequestBody AnalyzeRequest request) {

        AnalysisJob job = analysisService.createJob(request);

        // Async process
        analysisService.processJob(job.getId());

        return new AnalyzeResponse(
                job.getId(),
                job.getStatus().name(),
                "Analysis started successfully",
                null,   // score (not ready yet)
                null,   // grade
                null    // summary
        );
    }

    // 📊 GET RESULT
    @GetMapping("/job/{id}")
    public AnalysisJobResponse getJob(@PathVariable String id) {

        AnalysisJob job = analysisService.getJobById(id);

        return AnalysisJobResponse.from(job);
    }
}