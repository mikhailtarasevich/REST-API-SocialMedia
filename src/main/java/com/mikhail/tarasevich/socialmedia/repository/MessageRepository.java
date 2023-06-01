package com.mikhail.tarasevich.socialmedia.repository;

import com.mikhail.tarasevich.socialmedia.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

    @Query("SELECT m FROM Message m WHERE m.fromUser.id = :userOneId AND m.toUser.id = :userTwoId OR " +
            "m.fromUser.id = :userTwoId AND m.toUser.id = :userOneId ORDER BY m.createdAt")
    List<Message> findMessagesRelateToUsers(int userOneId, int userTwoId);

}
