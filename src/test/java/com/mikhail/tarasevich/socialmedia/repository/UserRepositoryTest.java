package com.mikhail.tarasevich.socialmedia.repository;

import com.mikhail.tarasevich.socialmedia.config.SpringTestConfig;
import com.mikhail.tarasevich.socialmedia.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = SpringTestConfig.class)
class UserRepositoryTest {

    @Autowired
    private UserRepository ur;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void findUserFriends_inputUserId_expectedListFriends() {

        List<User> userList = ur.findUserFriends(5);

        assertEquals(1, userList.size());
        assertEquals(2, userList.get(0).getId());
    }

    @Test
    void areUsersFriends_inputUsersId_expectedOptionalUser() {

        Optional<User> user = ur.areUsersFriends(1,2);

        assertTrue(user.isPresent());
    }

    @Test
    void findUserByEmail_inputEmail_expectedOptionalUser() {

        String name = "John Smith";
        String email = "john.smith@example.com";

        Optional<User> user = ur.findUserByEmail(email);

        assertTrue(user.isPresent());
        assertEquals(name, user.get().getName());
        assertEquals(email, user.get().getEmail());
    }

    @Test
    void findUserFriendRequests_inputName_expectedOptionalUser() {

        List<User> userList = ur.findUserFriendRequests(1);

        assertEquals(1, userList.size());
        assertEquals(4, userList.get(0).getId());
    }

    @Test
    void findUserSubscriptionsById_inputId_expectedListUsers() {

        List<User> userList = ur.findUserFriendRequests(5);

        assertEquals(1, userList.size());
        assertEquals(4, userList.get(0).getId());
    }

    @Test
    @Sql(scripts = {"classpath:sql/schema.sql", "classpath:sql/data.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void rejectFriendship_inputIdAndFriendId_expectedChangeStatusIsAcceptedToFalse() {

        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "subscribers",
                "user_id = 1 AND sub_user_id = 2 AND is_accepted = 'REJECTED'"));

        ur.rejectFriendship(1, 2);

        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "subscribers",
                "user_id = 1 AND sub_user_id = 2 AND is_accepted = 'REJECTED'"));
    }

    @Test
    @Sql(scripts = {"classpath:sql/schema.sql", "classpath:sql/data.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void unsubscribeFromUser_inputIdAndSubId_expectedRowWhereIdAndSubId() {

        assertEquals(1, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "subscribers",
                "user_id = 1 AND sub_user_id = 2"));

        ur.unsubscribeFromUser(1, 2);

        assertEquals(0, JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "subscribers",
                "user_id = 1 AND sub_user_id = 2"));
    }

}
