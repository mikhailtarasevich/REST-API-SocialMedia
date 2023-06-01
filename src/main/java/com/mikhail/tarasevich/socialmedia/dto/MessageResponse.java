package com.mikhail.tarasevich.socialmedia.dto;

import com.mikhail.tarasevich.socialmedia.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder(setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageResponse {

    private int id;

    private User fromUser;

    private User toUser;

    private String message;

    private LocalDateTime createdAt;

}
