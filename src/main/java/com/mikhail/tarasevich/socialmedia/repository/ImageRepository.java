package com.mikhail.tarasevich.socialmedia.repository;

import com.mikhail.tarasevich.socialmedia.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Integer> {

    @Query(value = "SELECT id FROM images WHERE post_id = :id", nativeQuery = true)
    List<Integer> findImagesIdRelateToPost(@Param("id") int id);

}
