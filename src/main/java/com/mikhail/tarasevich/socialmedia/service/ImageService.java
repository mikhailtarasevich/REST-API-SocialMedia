package com.mikhail.tarasevich.socialmedia.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {

    int uploadImage(MultipartFile file, int postId, String email);

    byte[] downloadImage(int id);

    List<Integer> findImagesIdRelateToPost(int postId);

}
