package com.mikhail.tarasevich.socialmedia.service.mapper.impl;

import com.mikhail.tarasevich.socialmedia.dto.PostRequest;
import com.mikhail.tarasevich.socialmedia.dto.PostResponse;
import com.mikhail.tarasevich.socialmedia.entity.Post;
import com.mikhail.tarasevich.socialmedia.entity.User;
import com.mikhail.tarasevich.socialmedia.service.mapper.PostMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PostMapperImpl implements PostMapper {

    @Override
    public PostResponse toResponse(Post entity) {

        return PostResponse.builder()
                .withId(entity.getId())
                .withUserId(entity.getUser().getId())
                .withHeader(entity.getHeader())
                .withContent(entity.getContent())
                .withCreatedAt(entity.getCreatedAt())
                .build();
    }

    @Override
    public Post toEntity(PostRequest request) {

        return Post.builder()
                .withId(request.getId())
                .withUser(User.builder().withId(request.getUserId()).build())
                .withHeader(request.getHeader())
                .withContent(request.getContent())
                .withCreatedAt(LocalDateTime.now())
                .build();
    }

}
