package dev.dotto.lifoapi.payloads.job;

import dev.dotto.lifoapi.payloads.ItemDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StopWorkResponse {
    private String message;
    private String jobName;
    private Integer workedTime;
    private Long experienceGained;
    private Long goldGained;
    private Integer levelUps;
    private Boolean finished;
    private List<ItemDTO> itemsGained;
}
