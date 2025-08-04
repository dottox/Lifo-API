package dev.dotto.lifoapi.services;

import dev.dotto.lifoapi.payloads.job.JobDTO;
import dev.dotto.lifoapi.payloads.job.JobResponse;
import dev.dotto.lifoapi.payloads.job.StartWorkResponse;
import dev.dotto.lifoapi.payloads.job.StopWorkResponse;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface JobService {
    JobResponse getAllJobs(Integer pageNumber, Integer pageSize, String sortBy, String sortDirection);

    JobDTO getJobById(@NotNull @Positive Long jobId);

    StartWorkResponse startWorking(@NotNull @Positive Long jobId);

    StopWorkResponse stopWorking();
}
