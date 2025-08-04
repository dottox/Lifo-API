package dev.dotto.lifoapi.payloads.job;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobDTO {
    private Long jobId;
    private String jobName;
    private Integer workingTime; // In seconds
    private Long experience;
    private Long gold;
    private Boolean premium;
}
