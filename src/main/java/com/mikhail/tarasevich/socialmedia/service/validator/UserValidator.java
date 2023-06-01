package com.mikhail.tarasevich.socialmedia.service.validator;

import com.mikhail.tarasevich.socialmedia.dto.UserRequest;

public interface UserValidator {

    void validateName(UserRequest request);

    void validateEmail(UserRequest request);

    void validatePassword(UserRequest request);

}
