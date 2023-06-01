package com.mikhail.tarasevich.socialmedia.service.mapper.impl;

import com.mikhail.tarasevich.socialmedia.dto.MessageRequest;
import com.mikhail.tarasevich.socialmedia.dto.MessageResponse;
import com.mikhail.tarasevich.socialmedia.entity.Message;
import com.mikhail.tarasevich.socialmedia.entity.User;
import com.mikhail.tarasevich.socialmedia.service.mapper.MessageMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MessageMapperImpl implements MessageMapper {

    @Override
    public MessageResponse toResponse(Message entity) {

        return MessageResponse.builder()
                .withId(entity.getId())
                .withFromUser(entity.getFromUser())
                .withToUser(entity.getToUser())
                .withMessage(entity.getMessage())
                .withCreatedAt(entity.getCreatedAt())
                .build();
    }

    @Override
    public Message toEntity(MessageRequest request) {

        return Message.builder()
                .withFromUser(User.builder().withId(request.getFromUserId()).build())
                .withToUser(User.builder().withId(request.getToUserId()).build())
                .withMessage(request.getMessage())
                .withCreatedAt(LocalDateTime.now())
                .build();
    }

}
