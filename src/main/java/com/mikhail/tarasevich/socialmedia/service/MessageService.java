package com.mikhail.tarasevich.socialmedia.service;

import com.mikhail.tarasevich.socialmedia.dto.MessageRequest;
import com.mikhail.tarasevich.socialmedia.dto.MessageResponse;

import java.util.List;

public interface MessageService {

    void createMessage (MessageRequest request);

    List<MessageResponse> findMessagesRelateToUsers(int userOneId, int userTwoId);

}
