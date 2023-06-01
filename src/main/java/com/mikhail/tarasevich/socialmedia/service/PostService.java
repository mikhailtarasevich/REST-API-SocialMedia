package com.mikhail.tarasevich.socialmedia.service;

import com.mikhail.tarasevich.socialmedia.dto.PostRequest;
import com.mikhail.tarasevich.socialmedia.dto.PostResponse;

import java.util.List;

public interface PostService {

    PostResponse findPostById (int id);

    PostResponse createPost(PostRequest request, String email);

    void updatePost(PostRequest request, String email);

    void deletePostById(int id);

    List<PostResponse> findAllPostsRelateToUser(int id);

    List<PostResponse> findLastPostOfSubscriptions(int id);

    List<PostResponse> findLastPostOfSubscriptions(int id, int itemsPerPage, int page);

}
