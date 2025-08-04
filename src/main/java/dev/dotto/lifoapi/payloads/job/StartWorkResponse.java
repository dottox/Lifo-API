package dev.dotto.lifoapi.payloads.job;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StartWorkResponse {
    private String message;
    private String jobName;
    private Integer workingTime;
    private Long workingUntil;
}
