package com.mikhail.tarasevich.socialmedia.controller;

import com.mikhail.tarasevich.socialmedia.service.ImageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/post")
@Api(tags = "Контроллер для добавления изображений к публикации")
public class PostController {

    private final ImageService imageService;

    @Autowired
    public PostController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/{id}/image")
    @ApiOperation(value = "Добавить изображение к публикации", notes = "Загружает изображение в базу данных и ассоциирует его с существующим постом по ID (Метод работает при передачи картинки из postman)")
    public ResponseEntity<String> addImageToPost(@ApiIgnore Principal user,
                                                 @ApiParam(value = "ID публикации в базе данных", example = "1", required = true) @PathVariable("id") int id,
                                                 @ApiParam(value = "Прикрепленная картинка", required = true) @RequestParam("image") MultipartFile image) {

        int imageId = imageService.uploadImage(image, id, user.getName());

        return ResponseEntity.status(HttpStatus.OK).body("Image with id = " + imageId +
                " was successfully added to post with id = " + id);
    }

}
