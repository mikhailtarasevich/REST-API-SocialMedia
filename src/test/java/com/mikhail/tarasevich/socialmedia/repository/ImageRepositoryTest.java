package com.mikhail.tarasevich.socialmedia.repository;

import com.mikhail.tarasevich.socialmedia.config.SpringTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = SpringTestConfig.class)
class ImageRepositoryTest {

    @Autowired
    private ImageRepository ir;


    @Test
    void findImagesIdRelateToPost_inputPostId_expectedListIds() {

        List<Integer> imagesId = ir.findImagesIdRelateToPost(1);

        assertEquals(2, imagesId.size());
        assertEquals(1, imagesId.get(0));
        assertEquals(2, imagesId.get(1));
    }

}
