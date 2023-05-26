package com.mikhail.tarasevich.socialmedia.repository;

import com.mikhail.tarasevich.socialmedia.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {

    List<Post> findPostsByUserId(int id);

    @Query(value = "SELECT p.* " +
            "FROM subscribers s " +
            "INNER JOIN " +
            "(SELECT user_id, MAX(created_at) AS max_created_at FROM posts GROUP BY user_id) " +
            "AS latest_posts ON s.sub_user_id = latest_posts.user_id " +
            "INNER JOIN posts p ON latest_posts.user_id = p.user_id AND latest_posts.max_created_at = p.created_at " +
            "WHERE s.user_id = :userId", nativeQuery = true)
    List<Post> findLatestPostsOfSubscribers(@Param("userId") int userId);

    @Query(value = "SELECT p.* " +
            "FROM subscribers s " +
            "INNER JOIN " +
            "(SELECT user_id, MAX(created_at) AS max_created_at FROM posts GROUP BY user_id) " +
            "AS latest_posts ON s.sub_user_id = latest_posts.user_id " +
            "INNER JOIN posts p ON latest_posts.user_id = p.user_id AND latest_posts.max_created_at = p.created_at " +
            "WHERE s.user_id = :userId " +
            "ORDER BY p.created_at DESC " +
            "LIMIT :limit OFFSET :offset",
            nativeQuery = true)
    List<Post> findLatestPostsOfSubscribers(@Param("userId") int userId, @Param("limit") int limit, @Param("offset") int offset);

}
