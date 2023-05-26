package com.mikhail.tarasevich.socialmedia.controller;

import com.mikhail.tarasevich.socialmedia.dto.MessageRequest;
import com.mikhail.tarasevich.socialmedia.dto.MessageResponse;
import com.mikhail.tarasevich.socialmedia.dto.PostRequest;
import com.mikhail.tarasevich.socialmedia.dto.PostResponse;
import com.mikhail.tarasevich.socialmedia.dto.UserResponse;
import com.mikhail.tarasevich.socialmedia.entity.User;
import com.mikhail.tarasevich.socialmedia.service.MessageService;
import com.mikhail.tarasevich.socialmedia.service.PostService;
import com.mikhail.tarasevich.socialmedia.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.security.Principal;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @InjectMocks
    private UserController userController;
    @Mock
    private UserService userService;
    @Mock
    private PostService postService;
    @Mock
    private MessageService messageService;
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(GlobalExceptionHandler.class)
                .build();
    }

    @Test
    void showUser_authenticatedUser_returnUserResponse() throws Exception {
        Principal principal = () -> "john@example.com";

        UserResponse expectedResponse = UserResponse.builder().withId(1).withName("John").withEmail("john@example.com").build();

        when(userService.findUserByEmail("john@example.com")).thenReturn(expectedResponse);

        mockMvc.perform(get("/api/v1/user/")
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\": 1,\"name\":\"John\",\"email\":\"john@example.com\"}"));

        verify(userService, times(1)).findUserByEmail("john@example.com");
        verifyNoMoreInteractions(userService);
    }

    @Test
    void showUserFriends_authenticatedUser_returnListOfUserResponses() throws Exception {
        Principal principal = () -> "john@example.com";

        UserResponse friend1 = UserResponse.builder().withId(2).withName("Friend 1").withEmail("friend1@example.com").build();
        UserResponse friend2 = UserResponse.builder().withId(3).withName("Friend 2").withEmail("friend2@example.com").build();

        List<UserResponse> expectedResponse = List.of(friend1, friend2);

        when(userService.findUserByEmail("john@example.com")).thenReturn(UserResponse.builder().withId(1).withName("John").withEmail("john@example.com").build());
        when(userService.findUserFriends(1)).thenReturn(expectedResponse);

        mockMvc.perform(get("/api/v1/user/friend")
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\": 2,\"name\":\"Friend 1\",\"email\":\"friend1@example.com\"},{\"id\": 3,\"name\":\"Friend 2\",\"email\":\"friend2@example.com\"}]"));

        verify(userService, times(1)).findUserByEmail("john@example.com");
        verify(userService, times(1)).findUserFriends(1);
        verifyNoMoreInteractions(userService);
    }

    @Test
    void showUserFriendRequests_authenticatedUser_returnListOfUserResponses() throws Exception {
        Principal principal = () -> "john@example.com";

        UserResponse request1 = UserResponse.builder().withId(2).withName("Request 1").withEmail("request1@example.com").build();
        UserResponse request2 = UserResponse.builder().withId(3).withName("Request 2").withEmail("request2@example.com").build();

        List<UserResponse> expectedResponse = List.of(request1, request2);

        when(userService.findUserByEmail("john@example.com")).thenReturn(UserResponse.builder().withId(1).withName("John").withEmail("john@example.com").build());
        when(userService.findUserFriendRequests(1)).thenReturn(expectedResponse);

        mockMvc.perform(get("/api/v1/user/friend/request")
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\": 2,\"name\":\"Request 1\",\"email\":\"request1@example.com\"},{\"id\": 3,\"name\":\"Request 2\",\"email\":\"request2@example.com\"}]"));

        verify(userService, times(1)).findUserByEmail("john@example.com");
        verify(userService, times(1)).findUserFriendRequests(1);
        verifyNoMoreInteractions(userService);
    }

    @Test
    void sendFriendRequest_authenticatedUser_sendFriendRequestAndReturnResponse() throws Exception {
        Principal principal = () -> "john@example.com";

        int friendId = 2;

        UserResponse userResponse = UserResponse.builder().withId(1).withName("John").withEmail("john@example.com").build();

        when(userService.findUserByEmail("john@example.com")).thenReturn(userResponse);

        mockMvc.perform(post("/api/v1/user/friend/request")
                        .principal(principal)
                        .param("friendId", String.valueOf(friendId)))
                .andExpect(status().isOk())
                .andExpect(content().string("Friend request from user with id = 1 to user with id = 2 has been successfully sent"));

        verify(userService, times(1)).findUserByEmail("john@example.com");
        verify(userService, times(1)).sendFriendRequest(1, friendId);
        verifyNoMoreInteractions(userService);
    }

    @Test
    void acceptFriendRequest_authenticatedUser_acceptFriendRequestAndReturnResponse() throws Exception {
        Principal principal = () -> "john@example.com";

        int friendId = 2;

        UserResponse userResponse = UserResponse.builder().withId(1).withName("John").withEmail("john@example.com").build();

        when(userService.findUserByEmail("john@example.com")).thenReturn(userResponse);

        mockMvc.perform(patch("/api/v1/user/friend/accept")
                        .principal(principal)
                        .param("friendId", String.valueOf(friendId)))
                .andExpect(status().isOk())
                .andExpect(content().string("Friend request from user with id = 2 to user with id = 1 has been accepted"));

        verify(userService, times(1)).findUserByEmail("john@example.com");
        verify(userService, times(1)).acceptFriendRequest(1, friendId);
        verifyNoMoreInteractions(userService);
    }

    @Test
    void rejectFriendRequest_authenticatedUser_rejectFriendRequestAndReturnResponse() throws Exception {
        Principal principal = () -> "john@example.com";

        int friendId = 2;

        UserResponse userResponse = UserResponse.builder().withId(1).withName("John").withEmail("john@example.com").build();

        when(userService.findUserByEmail("john@example.com")).thenReturn(userResponse);

        mockMvc.perform(patch("/api/v1/user/friend/reject")
                        .principal(principal)
                        .param("friendId", String.valueOf(friendId)))
                .andExpect(status().isOk())
                .andExpect(content().string("Friend request from user with id = 2 to user with id = 1 has been rejected"));

        verify(userService, times(1)).findUserByEmail("john@example.com");
        verify(userService, times(1)).rejectFriendRequest(1, friendId);
        verifyNoMoreInteractions(userService);
    }

    @Test
    void deleteUserFromFriends_authenticatedUser_deleteUserFromFriendsAndReturnResponse() throws Exception {
        Principal principal = () -> "john@example.com";

        int friendId = 2;

        UserResponse userResponse = UserResponse.builder().withId(1).withName("John").withEmail("john@example.com").build();

        when(userService.findUserByEmail("john@example.com")).thenReturn(userResponse);

        mockMvc.perform(delete("/api/v1/user/friend")
                        .principal(principal)
                        .param("friendId", String.valueOf(friendId)))
                .andExpect(status().isOk())
                .andExpect(content().string("User with id = 1 and user with id = 2 are no longer friends"));

        verify(userService, times(1)).findUserByEmail("john@example.com");
        verify(userService, times(1)).deleteUserFromFriends(1, friendId);
        verifyNoMoreInteractions(userService);
    }

    @Test
    void showUserSubscriptions_authenticatedUser_returnUserResponseList() throws Exception {
        Principal principal = () -> "john@example.com";

        UserResponse userResponse = UserResponse.builder().withId(1).withName("John").withEmail("john@example.com").build();

        when(userService.findUserByEmail("john@example.com")).thenReturn(userResponse);
        when(userService.findUserSubscriptions(1)).thenReturn(List.of(userResponse));

        mockMvc.perform(get("/api/v1/user/subscription")
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\": 1,\"name\":\"John\",\"email\":\"john@example.com\"}]"));

        verify(userService, times(1)).findUserByEmail("john@example.com");
        verify(userService, times(1)).findUserSubscriptions(1);
        verifyNoMoreInteractions(userService);
    }

    @Test
    void showAllUserPosts_authenticatedUser_returnPostResponseList() throws Exception {
        Principal principal = () -> "john@example.com";

        UserResponse userResponse = UserResponse.builder().withId(1).withName("John").withEmail("john@example.com").build();
        PostResponse postResponse = PostResponse.builder().withId(1).withContent("Hello").build();

        when(userService.findUserByEmail("john@example.com")).thenReturn(userResponse);
        when(postService.findAllPostsRelateToUser(1)).thenReturn(List.of(postResponse));

        mockMvc.perform(get("/api/v1/user/post")
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\": 1,\"content\":\"Hello\"}]"));

        verify(userService, times(1)).findUserByEmail("john@example.com");
        verify(postService, times(1)).findAllPostsRelateToUser(1);
        verifyNoMoreInteractions(userService, postService);
    }

    @Test
    void showLastPostOfSubscriptions_authenticatedUser_returnPostResponseList() throws Exception {
        Principal principal = () -> "john@example.com";

        int itemsPerPage = 3;
        int page = 1;

        UserResponse userResponse = UserResponse.builder().withId(1).withName("John").withEmail("john@example.com").build();
        PostResponse postResponse = PostResponse.builder().withId(1).withContent("Hello").build();

        when(userService.findUserByEmail("john@example.com")).thenReturn(userResponse);
        when(postService.findLastPostOfSubscriptions(1, itemsPerPage, page)).thenReturn(List.of(postResponse));

        mockMvc.perform(get("/api/v1/user/post/subscription")
                        .principal(principal)
                        .param("itemsPerPage", String.valueOf(itemsPerPage))
                        .param("page", String.valueOf(page)))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\": 1,\"content\":\"Hello\"}]"));

        verify(userService, times(1)).findUserByEmail("john@example.com");
        verify(postService, times(1)).findLastPostOfSubscriptions(1, itemsPerPage, page);
        verifyNoMoreInteractions(userService, postService);
    }

    @Test
    void addPost_authenticatedUser_validPostRequest_returnOkResponse() throws Exception {
        Principal principal = () -> "john@example.com";

        PostRequest postRequest = PostRequest.builder().withHeader("Hello World").withContent("Hello World").build();

        PostResponse postResponse = PostResponse.builder().withId(1).withHeader("Hello World").withContent("Hello World").build();

        when(postService.createPost(postRequest, "john@example.com")).thenReturn(postResponse);

        mockMvc.perform(post("/api/v1/user/post")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"header\":\"Hello World\",\"content\":\"Hello World\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Post was successfully saved with id = 1"));

        verify(postService, times(1)).createPost(postRequest, "john@example.com");
        verifyNoMoreInteractions(postService);
    }

    @Test
    void updatePost_authenticatedUser_validPostRequest_returnOkResponse() throws Exception {
        Principal principal = () -> "john@example.com";

        PostRequest postRequest = PostRequest.builder().withId(1).withHeader("Hello World").withContent("Hello World").build();

        mockMvc.perform(put("/api/v1/user/post")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"header\":\"Hello World\",\"content\":\"Hello World\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Post was successfully update"));

        verify(postService, times(1)).updatePost(postRequest, "john@example.com");
        verifyNoMoreInteractions(postService);
    }

    @Test
    void showChatHistory_authenticatedUser_validFriendId_returnMessageResponses() throws Exception {
        Principal principal = () -> "john@example.com";

        User user1 = User.builder().withId(1).withName("John").withEmail("john@example.com").build();
        UserResponse userResponse1 = UserResponse.builder().withId(1).withName("John").withEmail("john@example.com").build();
        User user2 = User.builder().withId(2).withName("Alice").withEmail("alice@example.com").build();

        List<MessageResponse> expectedResponses = List.of(
                MessageResponse.builder().withId(1).withFromUser(user1).withToUser(user2).withMessage("Hello").build(),
                MessageResponse.builder().withId(2).withFromUser(user2).withToUser(user1).withMessage("Hi").build()
        );

        when(messageService.findMessagesRelateToUsers(1, 2)).thenReturn(expectedResponses);
        when(userService.findUserByEmail(user1.getEmail())).thenReturn(userResponse1);

        mockMvc.perform(get("/api/v1/user/message")
                        .principal(principal)
                        .param("friendId", "2"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1,\"fromUser\":{\"id\":1,\"name\":\"John\",\"email\":\"john@example.com\"},\"toUser\":{\"id\":2,\"name\":\"Alice\",\"email\":\"alice@example.com\"},\"message\":\"Hello\"},{\"id\":2,\"fromUser\":{\"id\":2,\"name\":\"Alice\",\"email\":\"alice@example.com\"},\"toUser\":{\"id\":1,\"name\":\"John\",\"email\":\"john@example.com\"},\"message\":\"Hi\"}]"));

        verify(messageService, times(1)).findMessagesRelateToUsers(1, 2);
        verify(userService, times(1)).findUserByEmail(user1.getEmail());
        verifyNoMoreInteractions(messageService);
    }

    @Test
    void sendMessageToFriend_authenticatedUser_validRequest_messageSentSuccessfully() throws Exception {
        Principal principal = () -> "john@example.com";
        int friendId = 2;
        String message = "Hello!";

        UserResponse userResponse = UserResponse.builder().withId(1).withName("John").withEmail("john@example.com").build();

        when(userService.findUserByEmail(principal.getName())).thenReturn(userResponse);

        mockMvc.perform(post("/api/v1/user/message")
                        .principal(principal)
                        .param("friendId", String.valueOf(friendId))
                        .param("message", message))
                .andExpect(status().isOk())
                .andExpect(content().string("Message was successfully sent"));

        verify(userService, times(1)).findUserByEmail(principal.getName());
        verify(messageService, times(1)).createMessage(MessageRequest.builder().withFromUserId(userResponse.getId()).withToUserId(friendId).withMessage(message).build());
    }

}
