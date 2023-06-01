package com.mikhail.tarasevich.socialmedia.service.validator.impl;

import com.mikhail.tarasevich.socialmedia.dto.UserRequest;
import com.mikhail.tarasevich.socialmedia.repository.UserRepository;
import com.mikhail.tarasevich.socialmedia.service.exception.UserNotValidDataException;
import com.mikhail.tarasevich.socialmedia.service.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserValidatorImpl implements UserValidator {

    UserRepository userRepository;

    @Autowired
    public UserValidatorImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void validateName(UserRequest request){
        if (userRepository.findUserByName(request.getName()).isPresent()) {
            throw new UserNotValidDataException("User with name " + request.getName() + " already exist.");
        }
    }

    @Override
    public void validateEmail(UserRequest request){
        if (userRepository.findUserByEmail(request.getEmail()).isPresent()) {
            throw new UserNotValidDataException("User with email " + request.getEmail() + " already exist.");
        }
    }

    @Override
    public void validatePassword(UserRequest request){
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new UserNotValidDataException("Entered passwords are different.");
        }
    }

}
