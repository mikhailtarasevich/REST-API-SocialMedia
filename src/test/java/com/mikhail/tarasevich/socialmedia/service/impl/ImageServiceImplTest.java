package com.mikhail.tarasevich.socialmedia.service.impl;

import com.mikhail.tarasevich.socialmedia.entity.Image;
import com.mikhail.tarasevich.socialmedia.entity.Post;
import com.mikhail.tarasevich.socialmedia.entity.User;
import com.mikhail.tarasevich.socialmedia.repository.ImageRepository;
import com.mikhail.tarasevich.socialmedia.repository.PostRepository;
import com.mikhail.tarasevich.socialmedia.repository.UserRepository;
import com.mikhail.tarasevich.socialmedia.service.exception.IncorrectRequestDataException;
import com.mikhail.tarasevich.socialmedia.service.exception.PostNotValidDataException;
import com.mikhail.tarasevich.socialmedia.service.exception.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ImageServiceImplTest {

    @InjectMocks
    private ImageServiceImpl imageService;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserRepository userRepository;

    @Test
    void uploadImage_validData_saveImageAndReturnImageId() throws IOException {

        int postId = 1;
        String userEmail = "test@example.com";
        MultipartFile file = createMockMultipartFile();

        User user = User.builder()
                .withId(1)
                .withEmail(userEmail)
                .build();

        Post post = Post.builder()
                .withId(postId)
                .withUser(user)
                .build();

        byte[] imageData = {0};
        byte[] imageDataForSave = {120, -38, 99, 0, 0, 0, 1, 0, 1};

        Image image = Image.builder()
                .withId(1)
                .withName(file.getOriginalFilename())
                .withPost(post)
                .withType(file.getContentType())
                .withData(imageData)
                .build();

        Image imageForSave = Image.builder()
                .withName(file.getOriginalFilename())
                .withPost(post)
                .withType(file.getContentType())
                .withData(imageDataForSave)
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userRepository.findUserByEmail(userEmail)).thenReturn(Optional.of(user));
        when(file.getOriginalFilename()).thenReturn("test.jpg");
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getBytes()).thenReturn(imageData);
        when(imageRepository.save(imageForSave)).thenReturn(image);

        imageService.uploadImage(file, postId, userEmail);

        verify(postRepository, times(1)).findById(postId);
        verify(userRepository, times(1)).findUserByEmail(userEmail);
        verify(imageRepository, times(1)).save(imageForSave);
        verifyNoMoreInteractions(postRepository, userRepository, imageRepository);
    }

    @Test
    void uploadImage_postNotExist_Exception() {

        int postId = 1;
        String userEmail = "test@example.com";
        MultipartFile file = null;

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(IncorrectRequestDataException.class, () -> imageService.uploadImage(file, postId, userEmail));

        verify(postRepository, times(1)).findById(postId);
        verifyNoMoreInteractions(postRepository, userRepository, imageRepository);
    }

    @Test
    void uploadImage_UserNotExist_Exception() {

        int postId = 1;
        String userEmail = "test@example.com";
        MultipartFile file = null;

        when(postRepository.findById(postId)).thenReturn(Optional.of(Post.builder().build()));
        when(userRepository.findUserByEmail(userEmail)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> imageService.uploadImage(file, postId, userEmail));

        verify(postRepository, times(1)).findById(postId);
        verify(userRepository, times(1)).findUserByEmail(userEmail);
        verifyNoMoreInteractions(postRepository, userRepository, imageRepository);
    }

    @Test
    void uploadImage_postNorRelateToUser_Exception() {

        int postId = 1;
        String userEmail = "test@example.com";
        MultipartFile file = null;

        when(postRepository.findById(postId)).thenReturn(Optional.of(Post.builder().withUser(User.builder().withId(2).build()).build()));
        when(userRepository.findUserByEmail(userEmail)).thenReturn(Optional.of(User.builder().withId(1).build()));

        assertThrows(PostNotValidDataException.class, () -> imageService.uploadImage(file, postId, userEmail));

        verify(postRepository, times(1)).findById(postId);
        verify(userRepository, times(1)).findUserByEmail(userEmail);
        verifyNoMoreInteractions(postRepository, userRepository, imageRepository);
    }

    @Test
    void downloadImage_existingImageId_returnImageData() {

        int imageId = 1;
        byte[] imageData = {8,7,0,0,0};

        Image image = Image.builder()
                .withId(imageId)
                .withData(imageData)
                .build();

        when(imageRepository.findById(imageId)).thenReturn(Optional.of(image));

        imageService.downloadImage(imageId);

        verify(imageRepository, times(1)).findById(imageId);
        verifyNoMoreInteractions(imageRepository);
    }

    @Test
    void downloadImage_nonExistingImageId_returnImageData() {

        int imageId = 1;

        when(imageRepository.findById(imageId)).thenReturn(Optional.empty());

        imageService.downloadImage(imageId);

        verify(imageRepository, times(1)).findById(imageId);
        verifyNoMoreInteractions(imageRepository);
    }

    @Test
    void findImagesIdRelateToPost_existingPostId_returnImageIds() {

        int postId = 1;
        List<Integer> imageIds = List.of(1, 2, 3);

        when(imageRepository.findImagesIdRelateToPost(postId)).thenReturn(imageIds);

        List<Integer> result = imageService.findImagesIdRelateToPost(postId);

        assertEquals(imageIds, result);

        verify(imageRepository, times(1)).findImagesIdRelateToPost(postId);
        verifyNoMoreInteractions(imageRepository);
    }

    private MultipartFile createMockMultipartFile() {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test.jpg");
        when(file.getContentType()).thenReturn("image/jpeg");
        return file;
    }

}
