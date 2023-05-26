package com.mikhail.tarasevich.socialmedia.service.impl;

import com.mikhail.tarasevich.socialmedia.dto.PostRequest;
import com.mikhail.tarasevich.socialmedia.dto.PostResponse;
import com.mikhail.tarasevich.socialmedia.dto.UserResponse;
import com.mikhail.tarasevich.socialmedia.entity.Post;
import com.mikhail.tarasevich.socialmedia.entity.User;
import com.mikhail.tarasevich.socialmedia.repository.ImageRepository;
import com.mikhail.tarasevich.socialmedia.repository.PostRepository;
import com.mikhail.tarasevich.socialmedia.service.UserService;
import com.mikhail.tarasevich.socialmedia.service.exception.PostNotFoundException;
import com.mikhail.tarasevich.socialmedia.service.exception.PostNotValidDataException;
import com.mikhail.tarasevich.socialmedia.service.mapper.PostMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceImplTest {

    @InjectMocks
    private PostServiceImpl postService;
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserService userService;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private PostMapper mapper;

    @Test
    void findPostById_existingPostId_returnPostResponseWithImages() {

        int postId = 1;
        Post post = Post.builder()
                .withId(postId)
                .withHeader("Test Post")
                .withContent("This is a test post")
                .build();

        List<Integer> imageIds = List.of(1, 2, 3);

        PostResponse expectedResponse = PostResponse.builder()
                .withId(postId)
                .withHeader("Test Post")
                .withContent("This is a test post")
                .withImages(imageIds)
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(imageRepository.findImagesIdRelateToPost(postId)).thenReturn(imageIds);
        when(mapper.toResponse(post)).thenReturn(expectedResponse);

        PostResponse result = postService.findPostById(postId);

        assertNotNull(result);
        assertEquals(expectedResponse, result);
        verify(postRepository, times(1)).findById(postId);
        verify(imageRepository, times(1)).findImagesIdRelateToPost(postId);
        verify(mapper, times(1)).toResponse(post);
    }

    @Test
    void findPostById_nonExistingPostId_throwPostNotFoundException() {

        int nonExistingPostId = 100;

        when(postRepository.findById(nonExistingPostId)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.findPostById(nonExistingPostId));

        verify(postRepository, times(1)).findById(nonExistingPostId);
        verifyNoInteractions(imageRepository);
        verifyNoInteractions(mapper);
    }

    @Test
    void createPost_validRequestAndExistingUser_returnPostResponse() {

        int userId = 1;
        String email = "test@example.com";

        PostRequest request = PostRequest.builder()
                .withHeader("Test Post")
                .withContent("This is a test post")
                .build();

        UserResponse userResponse = UserResponse.builder()
                .withId(userId)
                .withName("Test User")
                .withEmail(email)
                .build();

        Post post = Post.builder()
                .withId(1)
                .withHeader("Test Post")
                .withContent("This is a test post")
                .withUser(User.builder().withId(userId).build())
                .build();

        PostResponse expectedResponse = PostResponse.builder()
                .withId(1)
                .withHeader("Test Post")
                .withContent("This is a test post")
                .build();

        when(userService.findUserByEmail(email)).thenReturn(userResponse);
        when(mapper.toEntity(request)).thenReturn(post);
        when(postRepository.save(post)).thenReturn(post);
        when(mapper.toResponse(post)).thenReturn(expectedResponse);

        PostResponse result = postService.createPost(request, email);

        assertNotNull(result);
        assertEquals(expectedResponse, result);
        verify(userService, times(1)).findUserByEmail(email);
        verify(mapper, times(1)).toEntity(request);
        verify(postRepository, times(1)).save(post);
        verify(mapper, times(1)).toResponse(post);
    }

    @Test
    void updatePost_validRequestAndPostBelongsToUser_updatePostAndReturnVoid() {

        int postId = 1;
        int userId = 1;
        String email = "test@example.com";
        PostRequest request = PostRequest.builder()
                .withId(postId)
                .withHeader("Updated Post")
                .withContent("This is an updated post")
                .build();

        UserResponse userResponse = UserResponse.builder()
                .withId(userId)
                .withName("Test User")
                .withEmail(email)
                .build();

        PostResponse postResponse = PostResponse.builder()
                .withId(postId)
                .withHeader("Original Post")
                .withContent("This is an original post")
                .withUserId(userId)
                .build();

        Post postEntity = Post.builder()
                .withId(postId)
                .withHeader("Updated Post")
                .withContent("This is an updated post")
                .withUser(User.builder().withId(userId).build())
                .build();

        when(userService.findUserByEmail(email)).thenReturn(userResponse);
        when(postRepository.findById(postId)).thenReturn(Optional.of(postEntity));
        when(imageRepository.findImagesIdRelateToPost(postId)).thenReturn(Collections.emptyList());
        when(mapper.toResponse(postEntity)).thenReturn(postResponse);
        when(mapper.toEntity(request)).thenReturn(postEntity);

        assertDoesNotThrow(() -> postService.updatePost(request, email));

        verify(userService, times(1)).findUserByEmail(email);
        verify(postRepository, times(1)).findById(postId);
        verify(mapper, times(1)).toEntity(request);
        verify(postRepository, times(1)).save(postEntity);
    }

    @Test
    void updatePost_validRequestAndPostDoesNotBelongToUser_throwPostNotValidDataException() {

        int postId = 1;
        int userId = 1;
        int otherUserId = 2;
        String email = "test@example.com";
        PostRequest request = PostRequest.builder()
                .withId(postId)
                .withHeader("Updated Post")
                .withContent("This is an updated post")
                .build();

        UserResponse userResponse = UserResponse.builder()
                .withId(otherUserId)
                .withName("Test User")
                .withEmail(email)
                .build();

        PostResponse postResponse = PostResponse.builder()
                .withId(postId)
                .withHeader("Original Post")
                .withContent("This is an original post")
                .withUserId(userId)
                .build();

        Post postEntity = Post.builder()
                .withId(postId)
                .withHeader("Updated Post")
                .withContent("This is an updated post")
                .withUser(User.builder().withId(userId).build())
                .build();

        when(userService.findUserByEmail(email)).thenReturn(userResponse);
        when(postRepository.findById(postId)).thenReturn(Optional.of(postEntity));
        when(imageRepository.findImagesIdRelateToPost(postId)).thenReturn(Collections.emptyList());
        when(mapper.toResponse(postEntity)).thenReturn(postResponse);

        assertThrows(PostNotValidDataException.class, () -> postService.updatePost(request, email));

        verify(userService, times(1)).findUserByEmail(email);
        verify(postRepository, times(1)).findById(postId);
        verifyNoMoreInteractions(userService, postRepository, mapper);
    }

    @Test
    void deletePostById_existingPostId_deletePost() {

        int postId = 1;
        Post post = Post.builder()
                .withId(postId)
                .withHeader("Test Post")
                .withContent("This is a test post")
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        assertDoesNotThrow(() -> postService.deletePostById(postId));

        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).deleteById(postId);
    }

    @Test
    void deletePostById_nonExistingPostId_throwPostNotFoundException() {

        int postId = 1;

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.deletePostById(postId));

        verify(postRepository, times(1)).findById(postId);
        verifyNoMoreInteractions(postRepository);
    }

    @Test
    void findAllPostsRelateToUser_existingUserId_returnListOfPostResponses() {

        int userId = 1;

        Post post1 = Post.builder()
                .withId(1)
                .withHeader("Post 1")
                .withContent("This is post 1")
                .withUser(User.builder().withId(userId).build())
                .build();

        Post post2 = Post.builder()
                .withId(2)
                .withHeader("Post 2")
                .withContent("This is post 2")
                .withUser(User.builder().withId(userId).build())
                .build();

        List<Post> posts = List.of(post1, post2);

        when(postRepository.findPostsByUserId(userId)).thenReturn(posts);
        when(imageRepository.findImagesIdRelateToPost(1)).thenReturn(Collections.emptyList());
        when(imageRepository.findImagesIdRelateToPost(2)).thenReturn(List.of(1, 2));

        PostResponse response1 = PostResponse.builder()
                .withId(1)
                .withHeader("Post 1")
                .withContent("This is post 1")
                .build();

        PostResponse response2 = PostResponse.builder()
                .withId(2)
                .withHeader("Post 2")
                .withContent("This is post 2")
                .withImages(List.of(1, 2))
                .build();

        when(mapper.toResponse(post1)).thenReturn(response1);
        when(mapper.toResponse(post2)).thenReturn(response2);

        List<PostResponse> result = postService.findAllPostsRelateToUser(userId);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(response1, result.get(0));
        assertEquals(response2, result.get(1));

        verify(postRepository, times(1)).findPostsByUserId(userId);
        verify(imageRepository, times(1)).findImagesIdRelateToPost(1);
        verify(imageRepository, times(1)).findImagesIdRelateToPost(2);
        verify(mapper, times(1)).toResponse(post1);
        verify(mapper, times(1)).toResponse(post2);
        verifyNoMoreInteractions(postRepository, imageRepository, mapper);
    }

    @Test
    void findAllPostsRelateToUser_nonExistingUserId_returnEmptyList() {

        int userId = 1;

        when(postRepository.findPostsByUserId(userId)).thenReturn(Collections.emptyList());

        List<PostResponse> result = postService.findAllPostsRelateToUser(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(postRepository, times(1)).findPostsByUserId(userId);
        verifyNoMoreInteractions(postRepository, imageRepository, mapper);
    }

    @Test
    void findLastPostOfSubscriptions_existingUserId_returnListOfPostResponses() {

        int userId = 1;

        Post post1 = Post.builder()
                .withId(1)
                .withHeader("Post 1")
                .withContent("This is post 1")
                .build();

        Post post2 = Post.builder()
                .withId(2)
                .withHeader("Post 2")
                .withContent("This is post 2")
                .build();

        List<Post> posts = List.of(post1, post2);

        when(postRepository.findLatestPostsOfSubscribers(userId)).thenReturn(posts);
        when(imageRepository.findImagesIdRelateToPost(1)).thenReturn(Collections.emptyList());
        when(imageRepository.findImagesIdRelateToPost(2)).thenReturn(List.of(1, 2));

        PostResponse response1 = PostResponse.builder()
                .withId(1)
                .withHeader("Post 1")
                .withContent("This is post 1")
                .build();

        PostResponse response2 = PostResponse.builder()
                .withId(2)
                .withHeader("Post 2")
                .withContent("This is post 2")
                .withImages(List.of(1, 2))
                .build();

        when(mapper.toResponse(post1)).thenReturn(response1);
        when(mapper.toResponse(post2)).thenReturn(response2);

        List<PostResponse> result = postService.findLastPostOfSubscriptions(userId);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(response1, result.get(0));
        assertEquals(response2, result.get(1));

        verify(postRepository, times(1)).findLatestPostsOfSubscribers(userId);
        verify(imageRepository, times(1)).findImagesIdRelateToPost(1);
        verify(imageRepository, times(1)).findImagesIdRelateToPost(2);
        verify(mapper, times(1)).toResponse(post1);
        verify(mapper, times(1)).toResponse(post2);
        verifyNoMoreInteractions(postRepository, imageRepository, mapper);
    }

    @Test
    void findLastPostOfSubscriptions_nonExistingUserId_returnEmptyList() {

        int userId = 1;

        when(postRepository.findLatestPostsOfSubscribers(userId)).thenReturn(Collections.emptyList());

        List<PostResponse> result = postService.findLastPostOfSubscriptions(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(postRepository, times(1)).findLatestPostsOfSubscribers(userId);
        verifyNoMoreInteractions(postRepository, imageRepository, mapper);
    }

    @Test
    void findLastPostOfSubscriptionsPageable_existingUserIdAndValidPagination_returnListOfPostResponses() {

        int userId = 1;
        int itemsPerPage = 10;
        int page = 1;

        Post post1 = Post.builder()
                .withId(1)
                .withHeader("Post 1")
                .withContent("This is post 1")
                .build();

        Post post2 = Post.builder()
                .withId(2)
                .withHeader("Post 2")
                .withContent("This is post 2")
                .build();

        List<Post> posts = List.of(post1, post2);

        when(postRepository.findLatestPostsOfSubscribers(userId, itemsPerPage, 0)).thenReturn(posts);
        when(imageRepository.findImagesIdRelateToPost(1)).thenReturn(Collections.emptyList());
        when(imageRepository.findImagesIdRelateToPost(2)).thenReturn(List.of(1, 2));

        PostResponse response1 = PostResponse.builder()
                .withId(1)
                .withHeader("Post 1")
                .withContent("This is post 1")
                .build();

        PostResponse response2 = PostResponse.builder()
                .withId(2)
                .withHeader("Post 2")
                .withContent("This is post 2")
                .withImages(List.of(1, 2))
                .build();

        when(mapper.toResponse(post1)).thenReturn(response1);
        when(mapper.toResponse(post2)).thenReturn(response2);

        List<PostResponse> result = postService.findLastPostOfSubscriptions(userId, itemsPerPage, page);

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(response1, result.get(0));
        assertEquals(response2, result.get(1));

        verify(postRepository, times(1)).findLatestPostsOfSubscribers(userId, itemsPerPage, 0);
        verify(imageRepository, times(1)).findImagesIdRelateToPost(1);
        verify(imageRepository, times(1)).findImagesIdRelateToPost(2);
        verify(mapper, times(1)).toResponse(post1);
        verify(mapper, times(1)).toResponse(post2);
        verifyNoMoreInteractions(postRepository, imageRepository, mapper);
    }

    @Test
    void findLastPostOfSubscriptionsPageable_nonExistingUserId_returnEmptyList() {

        int userId = 1;
        int itemsPerPage = 10;
        int page = 1;

        when(postRepository.findLatestPostsOfSubscribers(userId, itemsPerPage, 0)).thenReturn(Collections.emptyList());

        List<PostResponse> result = postService.findLastPostOfSubscriptions(userId, itemsPerPage, page);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(postRepository, times(1)).findLatestPostsOfSubscribers(userId, itemsPerPage, 0);
        verifyNoMoreInteractions(postRepository, imageRepository, mapper);
    }


}
