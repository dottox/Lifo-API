package dev.dotto.lifoapi.payloads;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserItemResponse {
    private int totalItems;
    private List<UserItemDTO> items;
}
