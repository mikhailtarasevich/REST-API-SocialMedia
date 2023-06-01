package com.mikhail.tarasevich.socialmedia.service.impl;

import com.mikhail.tarasevich.socialmedia.dto.UserRequest;
import com.mikhail.tarasevich.socialmedia.dto.UserResponse;
import com.mikhail.tarasevich.socialmedia.entity.User;
import com.mikhail.tarasevich.socialmedia.repository.UserRepository;
import com.mikhail.tarasevich.socialmedia.service.exception.IncorrectRequestDataException;
import com.mikhail.tarasevich.socialmedia.service.exception.UserNotFoundException;
import com.mikhail.tarasevich.socialmedia.service.mapper.UserMapper;
import com.mikhail.tarasevich.socialmedia.service.validator.UserValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserValidator userValidator;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder encoder;


    @Test
    void findAll_returnUserResponseList() {

        List<User> userList = new ArrayList<>();
        User user1 = User.builder().withId(1).withName("John").withEmail("john@example.com").withPassword("1111").build();
        userList.add(user1);

        User user2 = User.builder().withId(1).withName("Jane").withEmail("jane@example.com").withPassword("1111").build();
        userList.add(user2);

        when(userRepository.findAll()).thenReturn(userList);

        UserResponse userResponse1 = UserResponse.builder().withId(1).withName("John").withEmail("john@example.com").build();

        UserResponse userResponse2 = UserResponse.builder().withId(1).withName("Jane").withEmail("jane@example.com").build();

        when(userMapper.toResponse(user1)).thenReturn(userResponse1);
        when(userMapper.toResponse(user2)).thenReturn(userResponse2);

        List<UserResponse> result = userService.findAll();

        assertEquals(2, result.size());
        assertEquals(userResponse1, result.get(0));
        assertEquals(userResponse2, result.get(1));

        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(1)).toResponse(user1);
        verify(userMapper, times(1)).toResponse(user2);
    }

    @Test
    void findUserById_ExistingId_UserResponse() {

        int userId = 1;
        User user = User.builder()
                .withId(userId)
                .withName("John")
                .withEmail("john@example.com")
                .withPassword("1111")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserResponse expectedResponse = UserResponse.builder()
                .withId(userId)
                .withName("John")
                .withEmail("john@example.com")
                .build();

        when(userMapper.toResponse(user)).thenReturn(expectedResponse);

        UserResponse result = userService.findUserById(userId);

        assertEquals(expectedResponse, result);

        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, times(1)).toResponse(user);
    }

    @Test
    void findUserByEmail_existingEmail_returnUserResponse() {

        String email = "john@example.com";
        User user = User.builder()
                .withId(1)
                .withName("John")
                .withEmail(email)
                .withPassword("1111")
                .build();

        UserResponse expectedResponse = UserResponse.builder()
                .withId(1)
                .withName("John")
                .withEmail(email)
                .build();

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(expectedResponse);

        UserResponse result = userService.findUserByEmail(email);

        assertNotNull(result);
        assertEquals(expectedResponse, result);
        verify(userRepository, times(1)).findUserByEmail(email);
    }

    @Test
    void findUserByEmail_nonExistingEmail_throwUserNotFoundException() {

        String email = "nonexisting@example.com";

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findUserByEmail(email));
        verify(userRepository, times(1)).findUserByEmail(email);
    }

    @Test
    void saveUser_validUserRequest_userSavedSuccessfully() {

        UserRequest request = UserRequest.builder()
                .withName("John")
                .withEmail("john@example.com")
                .withPassword("1111")
                .build();

        User userForSave = User.builder()
                .withName("John")
                .withEmail("john@example.com")
                .withPassword("encodedPassword")
                .build();

        User savedUser = User.builder()
                .withId(1)
                .withName("John")
                .withEmail("john@example.com")
                .withPassword("encodedPassword")
                .build();

        UserResponse savedUserResponse = UserResponse.builder()
                .withId(1)
                .withName("John")
                .withEmail("john@example.com")
                .build();

        doNothing().when(userValidator).validateName(request);
        doNothing().when(userValidator).validateEmail(request);
        doNothing().when(userValidator).validatePassword(request);
        when(encoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userMapper.toEntity(request)).thenReturn(userForSave);
        when(userMapper.toResponse(savedUser)).thenReturn(savedUserResponse);
        when(userRepository.save(userForSave)).thenReturn(savedUser);

        UserResponse result = userService.saveUser(request);

        assertEquals(savedUser.getId(), result.getId());
        assertEquals(savedUser.getName(), result.getName());
        assertEquals(savedUser.getEmail(), result.getEmail());
        verify(userValidator, times(1)).validateName(request);
        verify(userValidator, times(1)).validateEmail(request);
        verify(userValidator, times(1)).validatePassword(request);
        verify(userRepository, times(1)).save(userForSave);
    }

    @Test
    void findUserById_NonExistingId_ThrowsException() {

        int userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findUserById(userId));

        verify(userRepository, times(1)).findById(userId);
        verifyNoInteractions(userMapper);
    }

    @Test
    void findUserFriends_existingUserId_returnListOfUserResponses() {

        int userId = 1;
        User friend1 = User.builder()
                .withId(2)
                .withName("Friend1")
                .withEmail("friend1@example.com")
                .build();
        User friend2 = User.builder()
                .withId(3)
                .withName("Friend2")
                .withEmail("friend2@example.com")
                .build();
        List<User> friends = List.of(friend1, friend2);
        List<UserResponse> expectedResponses = List.of(
                UserResponse.builder().withId(2).withName("Friend1").withEmail("friend1@example.com").build(),
                UserResponse.builder().withId(3).withName("Friend2").withEmail("friend2@example.com").build()
        );

        when(userRepository.findUserFriends(userId)).thenReturn(friends);
        when(userMapper.toResponse(friend1)).thenReturn(expectedResponses.get(0));
        when(userMapper.toResponse(friend2)).thenReturn(expectedResponses.get(1));

        List<UserResponse> result = userService.findUserFriends(userId);

        assertNotNull(result);
        assertEquals(expectedResponses.size(), result.size());
        assertEquals(expectedResponses, result);
        verify(userRepository, times(1)).findUserFriends(userId);
        verify(userMapper, times(1)).toResponse(friend1);
        verify(userMapper, times(1)).toResponse(friend2);
    }

    @Test
    void findUserFriendRequests_existingUserId_returnListOfUserResponses() {
        // Arrange
        int userId = 1;
        User friendRequest1 = User.builder()
                .withId(2)
                .withName("FriendRequest1")
                .withEmail("friendrequest1@example.com")
                .build();
        User friendRequest2 = User.builder()
                .withId(3)
                .withName("FriendRequest2")
                .withEmail("friendrequest2@example.com")
                .build();
        List<User> friendRequests = List.of(friendRequest1, friendRequest2);
        List<UserResponse> expectedResponses = List.of(
                UserResponse.builder().withId(2).withName("FriendRequest1").withEmail("friendrequest1@example.com").build(),
                UserResponse.builder().withId(3).withName("FriendRequest2").withEmail("friendrequest2@example.com").build()
        );

        when(userRepository.findUserFriendRequests(userId)).thenReturn(friendRequests);
        when(userMapper.toResponse(friendRequest1)).thenReturn(expectedResponses.get(0));
        when(userMapper.toResponse(friendRequest2)).thenReturn(expectedResponses.get(1));

        List<UserResponse> result = userService.findUserFriendRequests(userId);

        assertNotNull(result);
        assertEquals(expectedResponses.size(), result.size());
        assertEquals(expectedResponses, result);
        verify(userRepository, times(1)).findUserFriendRequests(userId);
        verify(userMapper, times(1)).toResponse(friendRequest1);
        verify(userMapper, times(1)).toResponse(friendRequest2);
    }

    @Test
    void findUserSubscriptions_existingId_returnsUserResponseList() {

        int userId = 1;
        User user2 = User.builder()
                .withId(2)
                .withName("User2")
                .withEmail("user2@example.com")
                .build();

        User user3 = User.builder()
                .withId(3)
                .withName("User3")
                .withEmail("user3@example.com")
                .build();

        List<User> subscriptions = List.of(user2, user3);

        when(userRepository.findUserSubscriptionsById(userId)).thenReturn(subscriptions);
        when(userMapper.toResponse(user2)).thenReturn(UserResponse.builder()
                .withId(2)
                .withName("User2")
                .withEmail("user2@example.com")
                .build());
        when(userMapper.toResponse(user3)).thenReturn(UserResponse.builder()
                .withId(3)
                .withName("User3")
                .withEmail("user3@example.com")
                .build());

        List<UserResponse> result = userService.findUserSubscriptions(userId);

        assertEquals(2, result.size());
        assertEquals("User2", result.get(0).getName());
        assertEquals("User3", result.get(1).getName());

        verify(userRepository, times(1)).findUserSubscriptionsById(userId);
        verify(userMapper, times(1)).toResponse(user2);
        verify(userMapper, times(1)).toResponse(user3);
    }

    @Test
    void sendFriendRequest_existingUsers_requestSentSuccessfully() {

        int userId = 1;
        int friendId = 2;
        User user = User.builder()
                .withId(userId)
                .withName("John")
                .withEmail("john@example.com")
                .withPassword("1111")
                .build();

        User friend = User.builder()
                .withId(friendId)
                .withName("Jane")
                .withEmail("jane@example.com")
                .withPassword("2222")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findById(friendId)).thenReturn(Optional.of(friend));
        when(userRepository.findUserFriends(userId)).thenReturn(Collections.emptyList());

        userService.sendFriendRequest(userId, friendId);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findById(friendId);
        verify(userRepository, times(1)).findUserFriends(userId);
        verify(userRepository, times(1)).sendFriendRequest(userId, friendId);
    }

    @Test
    void sendFriendRequest_userNotFound_throwUserNotFoundException() {

        int userId = 1;
        int friendId = 2;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.sendFriendRequest(userId, friendId));
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).findById(friendId);
        verify(userRepository, never()).findUserFriends(anyInt());
        verify(userRepository, never()).sendFriendRequest(anyInt(), anyInt());
    }

    @Test
    void sendFriendRequest_friendNotFound_throwUserNotFoundException() {

        int userId = 1;
        int friendId = 2;
        User user = User.builder()
                .withId(userId)
                .withName("John")
                .withEmail("john@example.com")
                .withPassword("1111")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findById(friendId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.sendFriendRequest(userId, friendId));
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findById(friendId);
        verify(userRepository, never()).findUserFriends(anyInt());
        verify(userRepository, never()).sendFriendRequest(anyInt(), anyInt());
    }

    @Test
    void sendFriendRequest_alreadyFriends_throwIncorrectRequestDataException() {

        int userId = 1;
        int friendId = 2;
        User user = User.builder()
                .withId(userId)
                .withName("John")
                .withEmail("john@example.com")
                .withPassword("1111")
                .build();

        User friend = User.builder()
                .withId(friendId)
                .withName("Jane")
                .withEmail("jane@example.com")
                .withPassword("2222")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findById(friendId)).thenReturn(Optional.of(friend));
        when(userRepository.findUserFriends(userId)).thenReturn(Collections.singletonList(friend));

        assertThrows(IncorrectRequestDataException.class, () -> userService.sendFriendRequest(userId, friendId));
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findById(friendId);
        verify(userRepository, times(1)).findUserFriends(userId);
        verify(userRepository, never()).sendFriendRequest(anyInt(), anyInt());
    }

    @Test
    void acceptFriendRequest_existingFriendRequest_requestAcceptedSuccessfully() {

        int userId = 1;
        int friendId = 2;

        User user = User.builder()
                .withId(userId)
                .withName("John")
                .withEmail("john@example.com")
                .withPassword("1111")
                .build();

        User friend = User.builder()
                .withId(friendId)
                .withName("Jane")
                .withEmail("jane@example.com")
                .withPassword("2222")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findById(friendId)).thenReturn(Optional.of(friend));
        when(userRepository.findUserFriendRequests(userId)).thenReturn(Collections.singletonList(friend));

        userService.acceptFriendRequest(userId, friendId);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findById(friendId);
        verify(userRepository, times(1)).findUserFriendRequests(userId);
        verify(userRepository, times(1)).addUserToFriends(userId, friendId);
        verify(userRepository, times(1)).addUserToFriends(friendId, userId);
    }

    @Test
    void acceptFriendRequest_friendRequestNotFound_throwIncorrectRequestDataException() {

        int userId = 1;
        int friendId = 2;

        User user = User.builder()
                .withId(userId)
                .withName("John")
                .withEmail("john@example.com")
                .withPassword("1111")
                .build();

        User friend = User.builder()
                .withId(friendId)
                .withName("Jane")
                .withEmail("jane@example.com")
                .withPassword("2222")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findById(friendId)).thenReturn(Optional.of(friend));
        when(userRepository.findUserFriendRequests(userId)).thenReturn(Collections.emptyList());

        assertThrows(IncorrectRequestDataException.class, () -> userService.acceptFriendRequest(userId, friendId));
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findById(friendId);
        verify(userRepository, times(1)).findUserFriendRequests(userId);
        verify(userRepository, never()).addUserToFriends(anyInt(), anyInt());
    }

    @Test
    void acceptFriendRequest_userNotFound_throwUserNotFoundException() {

        int userId = 1;
        int friendId = 2;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.acceptFriendRequest(userId, friendId));
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).findById(friendId);
        verify(userRepository, never()).findUserFriendRequests(anyInt());
        verify(userRepository, never()).addUserToFriends(anyInt(), anyInt());
    }

    @Test
    void acceptFriendRequest_friendNotFound_throwUserNotFoundException() {

        int userId = 1;
        int friendId = 2;

        User user = User.builder()
                .withId(userId)
                .withName("John")
                .withEmail("john@example.com")
                .withPassword("1111")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findById(friendId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.acceptFriendRequest(userId, friendId));
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findById(friendId);
        verify(userRepository, never()).findUserFriendRequests(anyInt());
        verify(userRepository, never()).addUserToFriends(anyInt(), anyInt());
    }

    @Test
    void rejectFriendRequest_existingFriendRequest_requestRejectedSuccessfully() {

        int userId = 1;
        int friendId = 2;

        User user = User.builder()
                .withId(userId)
                .withName("John")
                .withEmail("john@example.com")
                .withPassword("1111")
                .build();

        User friend = User.builder()
                .withId(friendId)
                .withName("Jane")
                .withEmail("jane@example.com")
                .withPassword("2222")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findById(friendId)).thenReturn(Optional.of(friend));
        when(userRepository.findUserFriendRequests(userId)).thenReturn(Collections.singletonList(friend));

        userService.rejectFriendRequest(userId, friendId);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findById(friendId);
        verify(userRepository, times(1)).findUserFriendRequests(userId);
        verify(userRepository, times(1)).rejectFriendship(friendId, userId);
    }

    @Test
    void deleteUserFromFriends_existingFriends_friendsDeletedSuccessfully() {

        int userId = 1;
        int friendId = 2;

        User user = User.builder()
                .withId(userId)
                .withName("John")
                .withEmail("john@example.com")
                .withPassword("1111")
                .build();

        User friend = User.builder()
                .withId(friendId)
                .withName("Jane")
                .withEmail("jane@example.com")
                .withPassword("2222")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findById(friendId)).thenReturn(Optional.of(friend));
        when(userRepository.findUserFriends(userId)).thenReturn(Collections.singletonList(friend));

        userService.deleteUserFromFriends(userId, friendId);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findById(friendId);
        verify(userRepository, times(1)).findUserFriends(userId);
        verify(userRepository, times(1)).unsubscribeFromUser(userId, friendId);
        verify(userRepository, times(1)).rejectFriendship(friendId, userId);
    }

    @Test
    void deleteUserFromFriends_notFriends_throwIncorrectRequestDataException() {

        int userId = 1;
        int friendId = 2;

        User user = User.builder()
                .withId(userId)
                .withName("John")
                .withEmail("john@example.com")
                .withPassword("1111")
                .build();

        User friend = User.builder()
                .withId(friendId)
                .withName("Jane")
                .withEmail("jane@example.com")
                .withPassword("2222")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findById(friendId)).thenReturn(Optional.of(friend));
        when(userRepository.findUserFriends(userId)).thenReturn(Collections.emptyList());

        assertThrows(IncorrectRequestDataException.class, () -> userService.deleteUserFromFriends(userId, friendId));
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findById(friendId);
        verify(userRepository, times(1)).findUserFriends(userId);
        verify(userRepository, never()).unsubscribeFromUser(anyInt(), anyInt());
        verify(userRepository, never()).rejectFriendship(anyInt(), anyInt());
    }

    @Test
    void deleteUserFromFriends_userNotFound_throwUserNotFoundException() {

        int userId = 1;
        int friendId = 2;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUserFromFriends(userId, friendId));
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).findById(friendId);
        verify(userRepository, never()).findUserFriends(anyInt());
        verify(userRepository, never()).unsubscribeFromUser(anyInt(), anyInt());
        verify(userRepository, never()).rejectFriendship(anyInt(), anyInt());
    }

    @Test
    void deleteUserFromFriends_friendNotFound_throwUserNotFoundException() {

        int userId = 1;
        int friendId = 2;

        User user = User.builder()
                .withId(userId)
                .withName("John")
                .withEmail("john@example.com")
                .withPassword("1111")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.findById(friendId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUserFromFriends(userId, friendId));
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).findById(friendId);
        verify(userRepository, never()).findUserFriends(anyInt());
        verify(userRepository, never()).unsubscribeFromUser(anyInt(), anyInt());
        verify(userRepository, never()).rejectFriendship(anyInt(), anyInt());
    }

}
