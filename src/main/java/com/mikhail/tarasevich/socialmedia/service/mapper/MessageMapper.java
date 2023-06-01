package com.mikhail.tarasevich.socialmedia.service.mapper;

import com.mikhail.tarasevich.socialmedia.dto.MessageRequest;
import com.mikhail.tarasevich.socialmedia.dto.MessageResponse;
import com.mikhail.tarasevich.socialmedia.entity.Message;

public interface MessageMapper {

    MessageResponse toResponse (Message entity);

    Message toEntity (MessageRequest request);

}
