package com.mikhail.tarasevich.socialmedia.repository;

import com.mikhail.tarasevich.socialmedia.config.SpringTestConfig;
import com.mikhail.tarasevich.socialmedia.entity.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = SpringTestConfig.class)
class MessageRepositoryTest {

    @Autowired
    private MessageRepository mr;


    @Test
    void findMessagesRelateToUsers_inputUsersId_expectedListMessages() {

        List<Message> messageList = mr.findMessagesRelateToUsers(1, 2);

        assertEquals(2, messageList.size());
        assertEquals("Hey, how are you?", messageList.get(0).getMessage());
        assertEquals("I'm good, thanks! How about you?", messageList.get(1).getMessage());
    }

}
