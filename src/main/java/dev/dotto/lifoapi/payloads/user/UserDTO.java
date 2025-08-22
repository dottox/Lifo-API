package dev.dotto.lifoapi.payloads.user;

import dev.dotto.lifoapi.config.AppConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;



@Data
@NoArgsConstructor
@AllArgsConstructor
// Display info about the account of the user. Not in game information.
public class UserDTO {
    private Long userId;
    private String username;
    private String email;
    private Integer level;
    private Long currentLevelExperience;
    private Long totalExperience;
    private Long nextLevelExperience;
    private Long gold;
    private Date createdAt;
    private Boolean premium;
}
