package com.mikhail.tarasevich.socialmedia.service.impl;

import com.mikhail.tarasevich.socialmedia.dto.PostRequest;
import com.mikhail.tarasevich.socialmedia.dto.PostResponse;
import com.mikhail.tarasevich.socialmedia.dto.UserResponse;
import com.mikhail.tarasevich.socialmedia.entity.Post;
import com.mikhail.tarasevich.socialmedia.repository.ImageRepository;
import com.mikhail.tarasevich.socialmedia.repository.PostRepository;
import com.mikhail.tarasevich.socialmedia.service.PostService;
import com.mikhail.tarasevich.socialmedia.service.UserService;
import com.mikhail.tarasevich.socialmedia.service.exception.PostNotFoundException;
import com.mikhail.tarasevich.socialmedia.service.exception.PostNotValidDataException;
import com.mikhail.tarasevich.socialmedia.service.mapper.PostMapper;
import com.mikhail.tarasevich.socialmedia.util.PageableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserService userService;
    private final ImageRepository imageRepository;
    private final PostMapper mapper;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, UserService userService, ImageRepository imageRepository, PostMapper mapper) {
        this.postRepository = postRepository;
        this.userService = userService;
        this.imageRepository = imageRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponse findPostById(int id) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post with id = " + id + " does not exist"));

        List<Integer> imagesId = imageRepository.findImagesIdRelateToPost(id);

        PostResponse response = mapper.toResponse(post);
        response.setImages(imagesId);

        return response;
    }

    @Override
    public PostResponse createPost(PostRequest request, String email) {

        request.setId(0);

        UserResponse userResponse = userService.findUserByEmail(email);

        request.setUserId(userResponse.getId());

        return mapper.toResponse(postRepository.save(mapper.toEntity(request)));
    }

    @Override
    public void updatePost(PostRequest request, String email) {

        UserResponse userResponse = userService.findUserByEmail(email);

        PostResponse postResponse = findPostById(request.getId());

        if (postResponse.getUserId() != userResponse.getId())
            throw new PostNotValidDataException("User tries to update a post that does not belong to them");

        request.setUserId(userResponse.getId());

        mapper.toResponse(postRepository.save(mapper.toEntity(request)));
    }

    @Override
    public void deletePostById(int id) {

        postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post with id = " + id + " does not exist"));

        postRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponse> findAllPostsRelateToUser(int id) {

        return postRepository.findPostsByUserId(id).stream()
                .map(p -> {
                    PostResponse postResponse = mapper.toResponse(p);
                    List<Integer> imagesId = imageRepository.findImagesIdRelateToPost(p.getId());
                    postResponse.setImages(imagesId);
                    return postResponse;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponse> findLastPostOfSubscriptions(int id) {

        return postRepository.findLatestPostsOfSubscribers(id).stream()
                .map(p -> {
                    PostResponse postResponse = mapper.toResponse(p);
                    List<Integer> imagesId = imageRepository.findImagesIdRelateToPost(p.getId());
                    postResponse.setImages(imagesId);
                    return postResponse;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponse> findLastPostOfSubscriptions(int id, int itemsPerPage, int page) {

        itemsPerPage = PageableService.checkItemsPerPage(itemsPerPage);
        int offset = PageableService.getOffset(itemsPerPage, page);

        return postRepository.findLatestPostsOfSubscribers(id, itemsPerPage, offset).stream()
                .map(p -> {
                    PostResponse postResponse = mapper.toResponse(p);
                    List<Integer> imagesId = imageRepository.findImagesIdRelateToPost(p.getId());
                    postResponse.setImages(imagesId);
                    return postResponse;
                })
                .collect(Collectors.toList());
    }

}
