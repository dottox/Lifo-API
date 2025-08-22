package dev.dotto.lifoapi.payloads;

import dev.dotto.lifoapi.models.UserItemId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserItemDTO {
    private ItemDTO item;
    private Integer quantity;
}
