package com.mikhail.tarasevich.socialmedia.controller;

import com.mikhail.tarasevich.socialmedia.service.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    @InjectMocks
    private PostController postController;
    @Mock
    private ImageService imageService;
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(postController)
                .setControllerAdvice(GlobalExceptionHandler.class)
                .build();
    }

    @Test
    void addImageToPost_authenticatedUser_validRequest_imageAddedToPost() throws Exception {
        Principal principal = () -> "john@example.com";
        int postId = 1;
        MockMultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", "test image".getBytes());
        int imageId = 1;

        when(imageService.uploadImage(image, postId, "john@example.com")).thenReturn(imageId);

        mockMvc.perform(multipart("/api/v1/post/{id}/image", postId)
                        .file(image)
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(content().string("Image with id = " + imageId + " was successfully added to post with id = " + postId));

        verify(imageService, times(1)).uploadImage(image, postId, "john@example.com");
        verifyNoMoreInteractions(imageService);
    }

}