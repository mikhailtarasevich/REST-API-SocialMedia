package com.mikhail.tarasevich.socialmedia.service.mapper.impl;

import com.mikhail.tarasevich.socialmedia.dto.UserRequest;
import com.mikhail.tarasevich.socialmedia.dto.UserResponse;
import com.mikhail.tarasevich.socialmedia.entity.User;
import com.mikhail.tarasevich.socialmedia.service.mapper.UserMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements UserMapper {

    public UserResponse toResponse (User entity) {

        return UserResponse.builder()
                .withId(entity.getId())
                .withName(entity.getName())
                .withEmail(entity.getEmail())
                .build();
    }

    public User toEntity (UserRequest request) {

        return User.builder()
                .withId(request.getId())
                .withName(request.getName())
                .withEmail(request.getEmail())
                .withPassword(request.getPassword())
                .build();
    }

}
