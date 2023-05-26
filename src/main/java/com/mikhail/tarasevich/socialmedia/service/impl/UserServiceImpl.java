package com.mikhail.tarasevich.socialmedia.service.impl;

import com.mikhail.tarasevich.socialmedia.dto.UserRequest;
import com.mikhail.tarasevich.socialmedia.dto.UserResponse;
import com.mikhail.tarasevich.socialmedia.entity.User;
import com.mikhail.tarasevich.socialmedia.repository.UserRepository;
import com.mikhail.tarasevich.socialmedia.service.UserService;
import com.mikhail.tarasevich.socialmedia.service.exception.IncorrectRequestDataException;
import com.mikhail.tarasevich.socialmedia.service.exception.UserNotFoundException;
import com.mikhail.tarasevich.socialmedia.service.mapper.UserMapper;
import com.mikhail.tarasevich.socialmedia.service.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final UserMapper userMapper;
    private final PasswordEncoder encoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserValidator userValidator,
                           UserMapper userMapper, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.userValidator = userValidator;
        this.userMapper = userMapper;
        this.encoder = encoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {

        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findUserById(int id) {

        return userMapper.toResponse(userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("There is no user with id = " + id + " in DB")));
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse findUserByEmail(String email) {

        return userMapper.toResponse(userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("There is no user with email = " + email + " in DB")));
    }

    @Override
    public UserResponse saveUser(UserRequest request) {

        userValidator.validateName(request);
        userValidator.validateEmail(request);
        userValidator.validatePassword(request);

        request.setId(0);
        request.setPassword(encoder.encode(request.getPassword()));

        return userMapper.toResponse(userRepository.save(userMapper.toEntity(request)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findUserFriends(int id) {

        return userRepository.findUserFriends(id).stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findUserFriendRequests(int id) {

        return userRepository.findUserFriendRequests(id).stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findUserSubscriptions(int id) {

        return userRepository.findUserSubscriptionsById(id).stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void sendFriendRequest(int id, int friendId) {

        userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("There is no user with id = " + id + " in DB"));

        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new UserNotFoundException("There is no user with id = " + friendId + " in DB"));

        if (userRepository.findUserFriends(id).contains(friend)) {
            throw new IncorrectRequestDataException("User with id = " + id + " and user with id = " +
                    friendId + " are already friends.");
        }

        userRepository.sendFriendRequest(id, friendId);
    }

    @Override
    public void acceptFriendRequest(int id, int friendId) {

        checkFriendRequest(id, friendId);

        userRepository.addUserToFriends(id, friendId);
        userRepository.addUserToFriends(friendId, id);
    }

    @Override
    public void rejectFriendRequest(int id, int friendId) {

        checkFriendRequest(id, friendId);

        userRepository.rejectFriendship(friendId, id);
    }

    @Override
    public void deleteUserFromFriends(int id, int friendId) {

        userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("There is no user with id = " + id + " in DB"));

        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new UserNotFoundException("There is no user with id = " + friendId + " in DB"));

        if (!userRepository.findUserFriends(id).contains(friend)) {
            throw new IncorrectRequestDataException("User with id = " + id + " and user with id = " + friendId + " are not friends.");
        }

        userRepository.unsubscribeFromUser(id, friendId);
        userRepository.rejectFriendship(friendId, id);
    }

    private void checkFriendRequest(int toUserId, int fromUserId) {

        userRepository.findById(toUserId)
                .orElseThrow(() -> new UserNotFoundException("There is no user with id = " + toUserId + " in DB"));

        User friend = userRepository.findById(fromUserId)
                .orElseThrow(() -> new UserNotFoundException("There is no user with id = " + fromUserId + " in DB"));

        if (!userRepository.findUserFriendRequests(toUserId).contains(friend)) {
            throw new IncorrectRequestDataException("Request denied. Friend request from user with id = " +
                    fromUserId + " to user with id = " + toUserId + " does not exist.");
        }
    }

}
