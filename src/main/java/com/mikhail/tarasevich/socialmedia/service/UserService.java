package com.mikhail.tarasevich.socialmedia.service;

import com.mikhail.tarasevich.socialmedia.dto.UserRequest;
import com.mikhail.tarasevich.socialmedia.dto.UserResponse;

import java.util.List;

public interface UserService {

    List<UserResponse> findAll();

    UserResponse findUserById(int id);
    UserResponse findUserByEmail(String email);

    UserResponse saveUser(UserRequest request);

    List<UserResponse> findUserFriends(int id);

    List<UserResponse> findUserFriendRequests(int id);

    List<UserResponse> findUserSubscriptions(int id);

    void sendFriendRequest(int id, int friendId);

    void acceptFriendRequest(int id, int friendId);

    void rejectFriendRequest(int id, int friendId);

    void deleteUserFromFriends(int id, int friendId);

}
