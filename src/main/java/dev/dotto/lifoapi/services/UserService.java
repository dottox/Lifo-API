package dev.dotto.lifoapi.services;

import dev.dotto.lifoapi.payloads.UserDTO;

public interface UserService {
    UserDTO getLoggedUserDetails();
}
