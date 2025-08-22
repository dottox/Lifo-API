package dev.dotto.lifoapi.services;

import dev.dotto.lifoapi.models.UserItem;
import dev.dotto.lifoapi.payloads.UserItemDTO;
import dev.dotto.lifoapi.payloads.UserItemResponse;
import dev.dotto.lifoapi.payloads.user.UserDTO;

import java.util.List;

public interface UserService {
    UserDTO getUserDetails(Long userId);
    UserItemResponse getUserItems(Long userId);
}
