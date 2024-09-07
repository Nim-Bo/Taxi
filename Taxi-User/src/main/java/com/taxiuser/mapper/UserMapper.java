package com.taxiuser.mapper;

import com.taxiuser.dto.response.TravelerDTO;
import com.taxiuser.dto.response.UserResponseDTO;
import com.taxiuser.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserResponseDTO userToUserDTO(User user);

}
