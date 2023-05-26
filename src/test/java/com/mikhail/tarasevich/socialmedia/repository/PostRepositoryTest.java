package com.mikhail.tarasevich.socialmedia.repository;

import com.mikhail.tarasevich.socialmedia.config.SpringTestConfig;
import com.mikhail.tarasevich.socialmedia.entity.Post;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = SpringTestConfig.class)
class PostRepositoryTest {

    @Autowired
    private PostRepository pr;


    @Test
    void findLatestPostsOfSubscribers_inputUserId_expectedListWithPosts() {

        List<Post> posts = pr.findLatestPostsOfSubscribers(3);

        assertEquals(2, posts.size());
        assertEquals("Hello, world!", posts.get(0).getHeader());
        assertEquals("Just saying hello to everyone.", posts.get(0).getContent());
        assertEquals("Funny meme", posts.get(1).getHeader());
        assertEquals("This meme made me laugh so hard!", posts.get(1).getContent());
    }


    @Test
    void findLatestPostsOfSubscribersPageable_inputUserIdLimitOffset_expectedListWithPosts() {

        List<Post> posts = pr.findLatestPostsOfSubscribers(3, 1, 0);

        assertEquals(1, posts.size());
        assertEquals("Hello, world!", posts.get(0).getHeader());
        assertEquals("Just saying hello to everyone.", posts.get(0).getContent());
    }

}
