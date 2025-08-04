package dev.dotto.lifoapi.payloads.job;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StopWorkResponse {
    private String message;
    private String jobName;
    private Integer workedTime;
    private Long experienceGained;
    private Long goldGained;
    private Boolean finished;
}
