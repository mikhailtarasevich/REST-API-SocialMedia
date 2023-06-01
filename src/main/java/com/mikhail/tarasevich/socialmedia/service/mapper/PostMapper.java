package com.mikhail.tarasevich.socialmedia.service.mapper;

import com.mikhail.tarasevich.socialmedia.dto.PostRequest;
import com.mikhail.tarasevich.socialmedia.dto.PostResponse;
import com.mikhail.tarasevich.socialmedia.entity.Post;

public interface PostMapper {

    PostResponse toResponse (Post entity);

    Post toEntity (PostRequest request);

}
