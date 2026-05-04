package com.sijin.codereview.repository;

import com.sijin.codereview.model.AnalysisJob;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AnalysisJobRepository extends MongoRepository<AnalysisJob, String> {
}