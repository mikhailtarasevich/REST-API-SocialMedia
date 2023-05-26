package com.mikhail.tarasevich.socialmedia.service.mapper;

import com.mikhail.tarasevich.socialmedia.dto.UserRequest;
import com.mikhail.tarasevich.socialmedia.dto.UserResponse;
import com.mikhail.tarasevich.socialmedia.entity.User;

public interface UserMapper {

    UserResponse toResponse (User entity);

    User toEntity (UserRequest request);

}
