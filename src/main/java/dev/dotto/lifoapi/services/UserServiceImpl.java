package dev.dotto.lifoapi.services;

import dev.dotto.lifoapi.exceptions.ResourceNotFoundException;
import dev.dotto.lifoapi.models.User;
import dev.dotto.lifoapi.models.UserItem;
import dev.dotto.lifoapi.payloads.ItemDTO;
import dev.dotto.lifoapi.payloads.UserItemDTO;
import dev.dotto.lifoapi.payloads.UserItemResponse;
import dev.dotto.lifoapi.payloads.user.UserDTO;
import dev.dotto.lifoapi.repositories.UserRepository;
import dev.dotto.lifoapi.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDTO getUserDetails(Long userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public UserItemResponse getUserItems(Long userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        List<UserItemDTO> userItemDTOs = user.getItems().stream().map(ui -> {
            UserItemDTO uiDTO = modelMapper.map(ui, UserItemDTO.class);
            uiDTO.setItem(modelMapper.map(ui.getItem(), ItemDTO.class));
            return uiDTO;
        }).toList();

        return new UserItemResponse(
                user.getItems().size(),
                userItemDTOs
        );
    }
}
