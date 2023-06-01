package com.mikhail.tarasevich.socialmedia.service.impl;

import com.mikhail.tarasevich.socialmedia.dto.MessageRequest;
import com.mikhail.tarasevich.socialmedia.dto.MessageResponse;
import com.mikhail.tarasevich.socialmedia.repository.MessageRepository;
import com.mikhail.tarasevich.socialmedia.repository.UserRepository;
import com.mikhail.tarasevich.socialmedia.service.MessageService;
import com.mikhail.tarasevich.socialmedia.service.exception.IncorrectRequestDataException;
import com.mikhail.tarasevich.socialmedia.service.mapper.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MessageServiceImpl implements MessageService {

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final MessageMapper mapper;

    @Autowired
    public MessageServiceImpl(UserRepository userRepository, MessageRepository messageRepository, MessageMapper mapper) {
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
        this.mapper = mapper;
    }

    @Override
    public void createMessage(MessageRequest request) {

        userRepository.areUsersFriends(request.getFromUserId(), request.getToUserId())
                .orElseThrow(() -> new IncorrectRequestDataException("Users with ids " + request.getFromUserId() +
                        " and " + request.getToUserId() + " are not friends. Chatting is prohibited"));

        messageRepository.save(mapper.toEntity(request));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponse> findMessagesRelateToUsers(int userOneId, int userTwoId) {

        return messageRepository.findMessagesRelateToUsers(userOneId, userTwoId).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

}
