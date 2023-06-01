package com.mikhail.tarasevich.socialmedia.controller;

import com.mikhail.tarasevich.socialmedia.service.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(MockitoExtension.class)
class ImageControllerTest {

    @InjectMocks
    private ImageController imageController;
    @Mock
    private ImageService imageService;
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(imageController)
                .setControllerAdvice(GlobalExceptionHandler.class)
                .build();
    }

    @Test
    void downloadImage_validId_returnsImageBytes() throws Exception {
        byte[] imageData = {};
        int imageId = 1;

        when(imageService.downloadImage(imageId)).thenReturn(imageData);

        mockMvc.perform(get("/api/v1/image/1", imageId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(content().bytes(imageData));

        verify(imageService, times(1)).downloadImage(imageId);
        verifyNoMoreInteractions(imageService);
    }

}
