package com.mikhail.tarasevich.socialmedia.service.impl;

import com.mikhail.tarasevich.socialmedia.entity.Image;
import com.mikhail.tarasevich.socialmedia.entity.Post;
import com.mikhail.tarasevich.socialmedia.entity.User;
import com.mikhail.tarasevich.socialmedia.repository.ImageRepository;
import com.mikhail.tarasevich.socialmedia.repository.PostRepository;
import com.mikhail.tarasevich.socialmedia.repository.UserRepository;
import com.mikhail.tarasevich.socialmedia.service.ImageService;
import com.mikhail.tarasevich.socialmedia.service.exception.ImageIncorrectDataException;
import com.mikhail.tarasevich.socialmedia.service.exception.IncorrectRequestDataException;
import com.mikhail.tarasevich.socialmedia.service.exception.PostNotValidDataException;
import com.mikhail.tarasevich.socialmedia.service.exception.UserNotFoundException;
import com.mikhail.tarasevich.socialmedia.util.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@Transactional
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;

    private final PostRepository postRepository;

    private final UserRepository userRepository;

    @Autowired
    public ImageServiceImpl(ImageRepository imageRepository, PostRepository postRepository,
                            UserRepository userRepository) {
        this.imageRepository = imageRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    public int uploadImage(MultipartFile file, int postId, String userEmail) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IncorrectRequestDataException("The post with id = " + postId + " does not exist"));

        User user = userRepository.findUserByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("There is no user with email = " + userEmail + " in DB"));

        if (user.getId() != post.getUser().getId())
            throw new PostNotValidDataException("User tries add image to a post that does not belong to them");

        Image image;

        try {
            image = Image.builder()
                    .withName(file.getOriginalFilename())
                    .withPost(post)
                    .withType(file.getContentType())
                    .withData(ImageUtil.compressImage(file.getBytes()))
                    .build();
        } catch (IOException e) {
            throw new ImageIncorrectDataException("The image has incorrect data");
        }

        imageRepository.save(image);

        return image.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] downloadImage(int id) {

        return imageRepository.findById(id).map(image -> ImageUtil.decompressImage(image.getData()))
                .orElseThrow(() -> new IncorrectRequestDataException("There is no image with id = " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integer> findImagesIdRelateToPost(int postId) {

        return imageRepository.findImagesIdRelateToPost(postId);
    }

}
