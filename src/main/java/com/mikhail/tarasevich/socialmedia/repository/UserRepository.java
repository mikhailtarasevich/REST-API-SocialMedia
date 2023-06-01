package com.mikhail.tarasevich.socialmedia.repository;

import com.mikhail.tarasevich.socialmedia.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    @Query(value = "SELECT * FROM users u " +
            "JOIN subscribers s1 ON u.id = s1.user_id " +
            "JOIN subscribers s2 ON u.id = s2.sub_user_id " +
            "WHERE s1.sub_user_id = :id AND s2.user_id = :id AND s1.is_accepted = 'ACCEPTED' " +
            "ORDER BY u.id", nativeQuery = true)
    List<User> findUserFriends(@Param("id") int id);

    @Query(value = "SELECT * FROM users u JOIN subscribers s ON u.id = s.user_id " +
            "WHERE u.id = :firstId AND s.sub_user_id = :secondId AND s.is_accepted = 'ACCEPTED'", nativeQuery = true)
    Optional<User> areUsersFriends(@Param("firstId") int firstId, @Param("secondId") int secondId);

    Optional<User> findUserByEmail(@Param("email") String email);

    Optional<User> findUserByName(@Param("name") String name);

    @Query(value = "SELECT * FROM users u " +
            "WHERE u.id IN (SELECT sub_user_id FROM subscribers WHERE user_id = 1 AND is_accepted = 'PENDING') " +
            "ORDER BY u.id", nativeQuery = true)
    List<User> findUserFriendRequests(@Param("id") int id);

    @Query(value = "SELECT * FROM users u WHERE u.id IN (SELECT sub_user_id FROM subscribers WHERE user_id = :id)", nativeQuery = true)
    List<User> findUserSubscriptionsById(@Param("id") int id);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO subscribers (user_id, sub_user_id, is_accepted) VALUES (:friendId, :id, 'PENDING') " +
            "ON CONFLICT (user_id, sub_user_id) DO " +
            "UPDATE SET is_accepted = 'PENDING' " +
            "WHERE subscribers.is_accepted <> 'ACCEPTED' AND subscribers.user_id = :friendId " +
            "AND subscribers.sub_user_id = :id", nativeQuery = true)
    void sendFriendRequest(@Param("id") int id, @Param("friendId") int friendId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO subscribers (user_id, sub_user_id, is_accepted) VALUES (:id, :friendId, 'ACCEPTED') " +
            "ON CONFLICT (user_id, sub_user_id) DO " +
            "UPDATE SET is_accepted = 'ACCEPTED' " +
            "WHERE subscribers.user_id = :id AND subscribers.sub_user_id = :friendId", nativeQuery = true)
    void addUserToFriends(@Param("id") int id, @Param("friendId") int friendId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE subscribers SET is_accepted = 'REJECTED' " +
            "WHERE user_id = :id AND sub_user_id = :friendId", nativeQuery = true)
    void rejectFriendship(@Param("id") int id, @Param("friendId") int friendId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM subscribers WHERE user_id = :id AND sub_user_id = :anotherUserId", nativeQuery = true)
    void unsubscribeFromUser(@Param("id") int id, @Param("anotherUserId") int anotherUserId);

}