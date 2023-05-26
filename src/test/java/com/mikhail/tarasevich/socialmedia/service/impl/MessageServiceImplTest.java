package com.mikhail.tarasevich.socialmedia.service.impl;

import com.mikhail.tarasevich.socialmedia.dto.MessageRequest;
import com.mikhail.tarasevich.socialmedia.dto.MessageResponse;
import com.mikhail.tarasevich.socialmedia.entity.Message;
import com.mikhail.tarasevich.socialmedia.entity.User;
import com.mikhail.tarasevich.socialmedia.repository.MessageRepository;
import com.mikhail.tarasevich.socialmedia.repository.UserRepository;
import com.mikhail.tarasevich.socialmedia.service.exception.IncorrectRequestDataException;
import com.mikhail.tarasevich.socialmedia.service.mapper.MessageMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceImplTest {

    @InjectMocks
    private MessageServiceImpl messageService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private MessageMapper mapper;

    @Test
    void createMessage_usersAreFriends_saveMessage() {

        int fromUserId = 1;
        int toUserId = 2;

        MessageRequest request = MessageRequest.builder()
                .withFromUserId(fromUserId)
                .withToUserId(toUserId)
                .withMessage("Hello, friend!")
                .build();

        Message entity = Message.builder()
                .withFromUser(User.builder().withId(fromUserId).build())
                .withToUser(User.builder().withId(toUserId).build())
                .withMessage("Hello, friend!")
                .build();

        when(userRepository.areUsersFriends(fromUserId, toUserId)).thenReturn(Optional.of(User.builder().build()));
        when(mapper.toEntity(request)).thenReturn(entity);

        assertDoesNotThrow(() -> messageService.createMessage(request));

        verify(userRepository, times(1)).areUsersFriends(fromUserId, toUserId);
        verify(messageRepository, times(1)).save(entity);
        verifyNoMoreInteractions(userRepository, messageRepository);
    }

    @Test
    void createMessage_usersAreNotFriends_throwIncorrectRequestDataException() {

        int fromUserId = 1;
        int toUserId = 2;

        MessageRequest request = MessageRequest.builder()
                .withFromUserId(fromUserId)
                .withToUserId(toUserId)
                .withMessage("Hello, friend!")
                .build();

        when(userRepository.areUsersFriends(fromUserId, toUserId)).thenReturn(Optional.empty());

        assertThrows(IncorrectRequestDataException.class, () -> messageService.createMessage(request));

        verify(userRepository, times(1)).areUsersFriends(fromUserId, toUserId);
        verifyNoMoreInteractions(userRepository, messageRepository);
    }

    @Test
    void findMessagesRelateToUsers_validUsers_returnMessageResponses() {
        int userOneId = 1;
        int userTwoId = 2;

        Message message1 = Message.builder()
                .withId(1)
                .withFromUser(User.builder().withId(userOneId).build())
                .withToUser(User.builder().withId(userTwoId).build())
                .withMessage("Hello, userTwo!")
                .build();

        Message message2 = Message.builder()
                .withId(2)
                .withFromUser(User.builder().withId(userTwoId).build())
                .withToUser(User.builder().withId(userOneId).build())
                .withMessage("Hi, userOne!")
                .build();

        List<Message> messages = List.of(message1, message2);

        when(messageRepository.findMessagesRelateToUsers(userOneId, userTwoId)).thenReturn(messages);
        when(mapper.toResponse(message1)).thenReturn(MessageResponse.builder()
                .withId(1)
                .withFromUser(User.builder().withId(userOneId).build())
                .withToUser(User.builder().withId(userTwoId).build())
                .withMessage("Hello, userTwo!")
                .build());
        when(mapper.toResponse(message2)).thenReturn(MessageResponse.builder()
                .withId(2)
                .withFromUser(User.builder().withId(userTwoId).build())
                .withToUser(User.builder().withId(userOneId).build())
                .withMessage("Hi, userOne!")
                .build());

        List<MessageResponse> result = messageService.findMessagesRelateToUsers(userOneId, userTwoId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(userOneId, result.get(0).getFromUser().getId());
        assertEquals(userTwoId, result.get(0).getToUser().getId());
        assertEquals("Hello, userTwo!", result.get(0).getMessage());
        assertEquals(2, result.get(1).getId());
        assertEquals(userTwoId, result.get(1).getFromUser().getId());
        assertEquals(userOneId, result.get(1).getToUser().getId());
        assertEquals("Hi, userOne!", result.get(1).getMessage());

        verify(messageRepository, times(1)).findMessagesRelateToUsers(userOneId, userTwoId);
        verify(mapper, times(1)).toResponse(message1);
        verify(mapper, times(1)).toResponse(message2);
        verifyNoMoreInteractions(messageRepository, mapper);
    }

    @Test
    void findMessagesRelateToUsers_noMessages_returnEmptyList() {
        int userOneId = 1;
        int userTwoId = 2;

        when(messageRepository.findMessagesRelateToUsers(userOneId, userTwoId)).thenReturn(Collections.emptyList());

        List<MessageResponse> result = messageService.findMessagesRelateToUsers(userOneId, userTwoId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(messageRepository, times(1)).findMessagesRelateToUsers(userOneId, userTwoId);
        verifyNoMoreInteractions(messageRepository, mapper);
    }

}
