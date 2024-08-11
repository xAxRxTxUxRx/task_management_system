package com.artur.task_management_system.dto.mappers;

import com.artur.task_management_system.dto.UserCreationDTO;
import com.artur.task_management_system.dto.UserViewDTO;
import com.artur.task_management_system.model.User;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {
    UserViewDTO userToUserViewDTO(User user);
    User userCreationDTOtoUser(UserCreationDTO userCreationDTO);
}
