package com.mikhail.tarasevich.socialmedia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Builder(setterPrefix = "with")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageRequest {

    private int id;

    private int fromUserId;

    private int toUserId;

    @NotEmpty(message = "Message should not be empty")
    private String message;

}
