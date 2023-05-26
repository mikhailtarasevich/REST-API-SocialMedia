package com.mikhail.tarasevich.socialmedia.controller;

import com.mikhail.tarasevich.socialmedia.dto.MessageRequest;
import com.mikhail.tarasevich.socialmedia.dto.MessageResponse;
import com.mikhail.tarasevich.socialmedia.dto.PostRequest;
import com.mikhail.tarasevich.socialmedia.dto.PostResponse;
import com.mikhail.tarasevich.socialmedia.dto.UserResponse;
import com.mikhail.tarasevich.socialmedia.service.MessageService;
import com.mikhail.tarasevich.socialmedia.service.PostService;
import com.mikhail.tarasevich.socialmedia.service.UserService;
import com.mikhail.tarasevich.socialmedia.service.exception.PostNotValidDataException;
import com.mikhail.tarasevich.socialmedia.util.BindingResultValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@Api(tags = "Контроллер атунтифицированного пользователя")
public class UserController {

    private final UserService userService;
    private final PostService postService;
    private final MessageService messageService;

    @Autowired
    public UserController(UserService userService, PostService postService, MessageService messageService) {
        this.userService = userService;
        this.postService = postService;
        this.messageService = messageService;
    }

    @GetMapping("/")
    @ApiOperation(value = "Показать персональные данные аутентифицированного пользователя")
    UserResponse showUser(@ApiIgnore Principal user) {

        return userService.findUserByEmail(user.getName());
    }

    @GetMapping("/friend")
    @ApiOperation(value = "Показать список друзей аутентифицированного пользователя")
    List<UserResponse> showUserFriends(@ApiIgnore Principal user) {

        return userService.findUserFriends(userService.findUserByEmail(user.getName()).getId());
    }

    @GetMapping("/friend/request")
    @ApiOperation(value = "Показать список запросов в друзья аутентифицированного пользователя")
    List<UserResponse> showUserFriendRequests(@ApiIgnore Principal user) {

        return userService.findUserFriendRequests(userService.findUserByEmail(user.getName()).getId());
    }

    @PostMapping("/friend/request")
    @ApiOperation(value = "Отправить запрос в друзья другому пользователю")
    ResponseEntity<String> sendFriendRequest(@ApiIgnore Principal user,
                                             @ApiParam(value = "ID пользователя, которому будет отправлен запрос в друзья", example = "4", required = true) @RequestParam("friendId") int friendId) {

        UserResponse userResponse = userService.findUserByEmail(user.getName());

        userService.sendFriendRequest(userResponse.getId(), friendId);

        return ResponseEntity.status(HttpStatus.OK).body("Friend request from user with id = " + userResponse.getId() +
                " to user with id = " + friendId + " has been successfully sent");
    }

    @PatchMapping("/friend/accept")
    @ApiOperation(value = "Принять запрос в друзья от пользователя")
    ResponseEntity<String> acceptFriendRequest(@ApiIgnore Principal user,
                                               @ApiParam(value = "ID пользователя, чей запрос в друзья будет принят", example = "4", required = true) @RequestParam("friendId") int friendId) {

        UserResponse userResponse = userService.findUserByEmail(user.getName());

        userService.acceptFriendRequest(userResponse.getId(), friendId);

        return ResponseEntity.status(HttpStatus.OK).body("Friend request from user with id = " + friendId +
                " to user with id = " + userResponse.getId() + " has been accepted");
    }

    @PatchMapping("/friend/reject")
    @ApiOperation(value = "Отклонить запрос в друзья от пользователя")
    ResponseEntity<String> rejectFriendRequest(@ApiIgnore Principal user,
                                               @ApiParam(value = "ID пользователя, чей запрос в друзья будет откланен", example = "5", required = true) @RequestParam("friendId") int friendId) {

        UserResponse userResponse = userService.findUserByEmail(user.getName());

        userService.rejectFriendRequest(userResponse.getId(), friendId);

        return ResponseEntity.status(HttpStatus.OK).body("Friend request from user with id = " + friendId +
                " to user with id = " + userResponse.getId() + " has been rejected");
    }

    @DeleteMapping("/friend")
    @ApiOperation(value = "Удалить пользователя из списка друзей", notes = "Удаленный из списка друзей пользователь останется подписчиком")
    ResponseEntity<String> deleteUserFromFriends(@ApiIgnore Principal user,
                                                 @ApiParam(value = "ID пользователя, кто будет удален из списка друзей пользователя", example = "5", required = true) @RequestParam("friendId") int friendId) {

        UserResponse userResponse = userService.findUserByEmail(user.getName());

        userService.deleteUserFromFriends(userResponse.getId(), friendId);

        return ResponseEntity.status(HttpStatus.OK).body("User with id = " + userResponse.getId() +
                " and user with id = " + friendId + " are no longer friends");
    }

    @GetMapping("/subscription")
    @ApiOperation(value = "Показать список пользователей на которых подписан аутентифицированный пользователь")
    List<UserResponse> showUserSubscriptions(@ApiIgnore Principal user) {

        return userService.findUserSubscriptions(userService.findUserByEmail(user.getName()).getId());
    }

    @GetMapping("/post")
    @ApiOperation(value = "Показать список публикаций, опубликовнных аутентифицированным пользователем")
    public List<PostResponse> showAllUserPosts(@ApiIgnore Principal user) {

        return postService.findAllPostsRelateToUser(userService.findUserByEmail(user.getName()).getId());
    }

    @GetMapping("/post/subscription")
    @ApiOperation(value = "Показать список последних публикаций, опубликовнных подписчиками, аутентифицированным пользователем", notes = "Публикации выводятся постранично и будут отсортированы по времени создания от болле свежих к более старым")
    public List<PostResponse> showLastPostOfSubscriptions(@ApiIgnore Principal user,
                                                          @ApiParam(value = "Количество публикаций на странице", example = "3", required = true) @RequestParam("itemsPerPage") int itemsPerPage,
                                                          @ApiParam(value = "Номер страницы (отсчет от 1 стр.)", example = "1", required = true) @RequestParam("page") int page) {

        return postService.findLastPostOfSubscriptions(userService.findUserByEmail(user.getName()).getId(), itemsPerPage, page);
    }

    @PostMapping("/post")
    @ApiOperation(value = "Создать новую публикацию")
    public ResponseEntity<String> addPost(@ApiIgnore Principal user,
                                          @ApiParam(value = "Данные о публикации", required = true) @RequestBody @Valid PostRequest request,
                                          BindingResult bindingResult) {

        BindingResultValidator.checkErrorsInBindingResult(bindingResult, PostNotValidDataException.class);

        PostResponse response = postService.createPost(request, user.getName());

        return ResponseEntity.status(HttpStatus.OK).body("Post was successfully saved with id = " + response.getId());
    }

    @PutMapping("/post")
    @ApiOperation(value = "Редактировать публикацию")
    public ResponseEntity<String> updatePost(@ApiIgnore Principal user,
                                             @ApiParam(value = "Данные о публикации с указанием ID редактируемой публикации", required = true) @RequestBody @Valid PostRequest request,
                                             BindingResult bindingResult) {

        BindingResultValidator.checkErrorsInBindingResult(bindingResult, PostNotValidDataException.class);

        postService.updatePost(request, user.getName());

        return ResponseEntity.status(HttpStatus.OK).body("Post was successfully update");
    }

    @GetMapping("/message")
    @ApiOperation(value = "Показать историю сообщений между аутентифицированным пользователем и другим пользователем", notes = "Сообщения отсортированы по дате от более свежих")
    List<MessageResponse> showChatHistory(@ApiIgnore Principal user,
                                          @ApiParam(value = "ID пользователя", example = "2", required = true) @RequestParam("friendId") int friendId) {

        return messageService.findMessagesRelateToUsers(userService.findUserByEmail(user.getName()).getId(), friendId);
    }

    @PostMapping("/message")
    @ApiOperation(value = "Отправить сообщение от атентифицированного пользователя другу")
    ResponseEntity<String> sendMessageToFriend(@ApiIgnore Principal user,
                                               @ApiParam(value = "ID пользователя", example = "2", required = true) @RequestParam("friendId") int friendId,
                                               @ApiParam(value = "Содержание сообщения", example = "Hello!", required = true) @RequestParam("message") String message) {

        messageService.createMessage(MessageRequest.builder()
                .withFromUserId(userService.findUserByEmail(user.getName()).getId())
                .withToUserId(friendId)
                .withMessage(message)
                .build());

        return ResponseEntity.status(HttpStatus.OK).body("Message was successfully sent");
    }

}
