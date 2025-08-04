package dev.dotto.lifoapi.services;

import dev.dotto.lifoapi.models.User;
import dev.dotto.lifoapi.payloads.UserDTO;
import dev.dotto.lifoapi.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthUtil authUtil;

    @Override
    public UserDTO getLoggedUserDetails() {
        User user = authUtil.loggedInUser();
        return modelMapper.map(user, UserDTO.class);
    }
}
