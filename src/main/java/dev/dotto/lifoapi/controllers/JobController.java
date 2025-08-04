package dev.dotto.lifoapi.controllers;

import dev.dotto.lifoapi.config.AppConstants;
import dev.dotto.lifoapi.payloads.job.JobDTO;
import dev.dotto.lifoapi.payloads.job.JobResponse;
import dev.dotto.lifoapi.payloads.job.StartWorkResponse;
import dev.dotto.lifoapi.payloads.job.StopWorkResponse;
import dev.dotto.lifoapi.services.JobService;
import dev.dotto.lifoapi.util.PaginationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class JobController {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private JobService jobService;

    @Tag(name = "Job Management", description = "Operations related to job management")
    @Operation(summary = "Get all jobs", description = "Retrieve a list of all jobs available")
    @GetMapping("/jobs")
    public ResponseEntity<JobResponse> getAllJobs(
        @RequestParam(name = "pageNumber", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) Integer pageNumber,
        @RequestParam(name = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) Integer pageSize,
        @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_BY_JOB) String sortBy,
        @RequestParam(name = "sortDirection", defaultValue = AppConstants.SORT_DIRECTION) String sortDirection
    ) {
        // Validate page number and size
        if (!PaginationUtil.validatePaginationParams(pageNumber, pageSize, sortBy, sortDirection)) {;
            throw new IllegalArgumentException("Invalid pagination parameters");
        }

        JobResponse jobResponse = jobService.getAllJobs(pageNumber, pageSize, sortBy, sortDirection);
        return ResponseEntity.ok(jobResponse);
    }

    @Tag(name = "Job Management", description = "Operations related to job management")
    @Operation(summary = "Get job by ID", description = "Retrieve a job by its ID")
    @GetMapping("/jobs/{jobId}")
    public ResponseEntity<JobDTO> getJobById(
        @NotNull @Positive @PathVariable Long jobId
    ) {
        JobDTO jobDTO = jobService.getJobById(jobId);
        return ResponseEntity.ok(jobDTO);
    }

    @Tag(name = "Job Management", description = "Operations related to job management")
    @Operation(summary = "Start working", description = "Start working on a job by its ID")
    @PostMapping("/jobs/{jobId}/work")
    public ResponseEntity<StartWorkResponse> startWorking(
        @NotNull @Positive @PathVariable Long jobId
    ) {
        StartWorkResponse responseMessage = jobService.startWorking(jobId);
        return ResponseEntity.ok(responseMessage);
    }

    @Tag(name = "Job Management", description = "Operations related to job management")
    @Operation(summary = "Stop working", description = "Stop working on a job")
    @PostMapping("/jobs/stop")
    public ResponseEntity<StopWorkResponse> stopWorking() {
        StopWorkResponse responseMessage = jobService.stopWorking();
        return ResponseEntity.ok(responseMessage);
    }
}
